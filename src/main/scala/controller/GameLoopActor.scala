package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import controller.GameLoopActor.GameLoopCommands.{StartGame, UpdateLoop}
import model.GameData
import model.GameData.{GameEntity, GameSeq}
import model.actors.*
import model.common.Utilities.{MetaData, Speed, Sun}
import model.entities.*
import model.entities.WorldSpace.*
import model.waves.{Generator, WaveGenerator}
import view.Game

import scala.collection.immutable.Seq
import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.language.{implicitConversions, postfixOps}

object GameLoopActor:

  val waveGenerator: WaveGenerator = Generator()

  import GameLoopCommands.*
  import GameLoopUtils.*
  import GameLoopUtils.CollisionUtils.*

  def apply(
             viewActor: ActorRef[ViewMessage],
             entities: Seq[GameEntity[Entity]] = List.empty,
             metaData: MetaData = MetaData()
           ): Behavior[Command] = GameLoop(viewActor, entities, metaData).standardBehavior()

  private case class GameLoop(
                               viewActor: ActorRef[ViewMessage],
                               entities: Seq[GameEntity[Entity]],
                               metaData: MetaData
                             ):

    def standardBehavior(): Behavior[Command] =
      Behaviors.withTimers(timer =>
        Behaviors.receive((ctx, msg) => {
          msg match
            case StartGame() =>
              waveGenerator.resetWaves()
              startTimer(timer, UpdateLoop())
              startTimer(timer, UpdateResources(), resourceTimer)
              Behaviors.same

            case GameOver() =>
              Game.endGame()
              Behaviors.stopped

            case PauseGame() => pauseBehavior()

            case UpdateResources() =>
              startTimer(timer, UpdateResources(), resourceTimer)
              GameLoopActor(viewActor, entities, metaData + Sun.Normal.value)

            case ChangeGameSpeed(velocity) => GameLoopActor(viewActor, entities, metaData >>> velocity)

            case UpdateLoop() =>
              checkCollision(entities, ctx)
              updateAll(ctx, metaData.velocity, detectInterest(entities))
              startTimer(timer, UpdateLoop())
              GameLoopActor(viewActor, createWave(ctx, entities) ++ entities, metaData)

            case EntityUpdated(ref, entity) =>
              val newEntities = entities collect { case x if x.ref == ref => GameEntity(ref, entity) case x => x }
              render(ctx, viewActor, metaData, newEntities.map(_.entity).toList)
              GameLoopActor(viewActor, newEntities, metaData)

            case BulletSpawned(ref, bullet) =>
              GameLoopActor(viewActor, entities :+ GameEntity(ref, bullet), metaData)

            case PlacePlant(troop) =>
              troop.asInstanceOf[Plant] match
                case plant if metaData.sun < plant.cost => GameLoopActor(viewActor, entities, metaData)
                case _ =>
                  val newGameSeq = entities :+ GameEntity(ctx.spawnAnonymous(TroopActor(troop)), troop)
                  val newMetaData = metaData - troop.asInstanceOf[Plant].cost
                  GameLoopActor(viewActor, newGameSeq, newMetaData)

            case EntityDead(ref) => GameLoopActor(viewActor, entities filter { _.ref != ref }, metaData)

            case _ => Behaviors.same
        }))

    def pauseBehavior(): Behavior[Command] =
      Behaviors.receive((ctx, msg) => {
        msg match
          case ResumeGame() =>
            ctx.self ! UpdateLoop()
            GameLoopActor(viewActor, entities, metaData)

          case _ => Behaviors.same
      })

  object GameLoopCommands:
    trait Command

    case class StartGame() extends Command

    case class PauseGame() extends Command

    case class ResumeGame() extends Command

    case class UpdateLoop() extends Command

    case class GameOver() extends Command

    case class UpdateResources() extends Command

    case class ChangeGameSpeed(velocity: Speed) extends Command

    case class EntityDead(ref: ActorRef[ModelMessage]) extends Command

    case class EntityUpdated[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends Command

    case class BulletSpawned(ref: ActorRef[ModelMessage], bullet: Bullet) extends Command

    case class PlacePlant(troop: Troop) extends Command

  object GameLoopUtils:
    val resourceTimer: FiniteDuration = FiniteDuration(3, "seconds")

    def startTimer(
                    timer: TimerScheduler[Command],
                    msg: Command,
                    time: FiniteDuration = Speed.Normal.speed
                  ): Unit =
      timer.startSingleTimer(msg, time)

    def createWave(
                    ctx: ActorContext[Command],
                    entities: Seq[GameEntity[Entity]]
                  ): Seq[GameEntity[Entity]] =
      if isWaveOver(entities)
      then waveGenerator.generateNextWave.enemies.map(e => GameEntity(ctx.spawnAnonymous(TroopActor(e)), e))
      else List.empty

    def isWaveOver: Seq[GameEntity[Entity]] => Boolean = _ map (_.entity) collect { case enemy: Zombie => enemy } isEmpty

    def detectInterest(entities: Seq[GameEntity[Entity]]): Seq[(ActorRef[ModelMessage], Seq[Entity])] =
      for
        e <- entities
      yield
        (e.ref, for
          e2 <- entities
          if e != e2
          if e.entity isInterestedIn e2.entity
        yield e2.entity)

    def updateAll(
                   ctx: ActorContext[Command],
                   velocity: Speed,
                   interests: Seq[(ActorRef[ModelMessage], Seq[Entity])]
                 ): Unit =
      interests foreach (e => e._1 ! Update(velocity.speed, e._2.toList, ctx.self))

    def render(
                ctx: ActorContext[Command],
                viewActor: ActorRef[ViewMessage],
                metaData: MetaData,
                renderedEntities: List[Entity]
              ): Unit =
      viewActor ! Render(renderedEntities, ctx.self, metaData)

    object CollisionUtils:
      def checkCollision(entities: Seq[GameEntity[Entity]],
                         ctx: ActorContext[Command]
                        ): Unit =
        detectCollision(entities) filter (_._2.nonEmpty) foreach { e =>
          if e._1.entity hitMultipleTimes
          then e._2 foreach { r => sendCollisionMessage(e._1, r, ctx); sendCollisionMessage(r, e._1, ctx) }
          else {
            sendCollisionMessage(e._1, e._2.head, ctx);
            sendCollisionMessage(e._2.head, e._1, ctx)
          }
        }

      def sendCollisionMessage[A <: Entity, E <: Entity](
                                                          to: GameEntity[A],
                                                          from: GameEntity[E],
                                                          ctx: ActorContext[Command]
                                                        ): Unit =
        to.ref ! Collision(from.entity, ctx.self)

      def detectCollision(entities: Seq[GameEntity[Entity]]): Seq[(GameEntity[Bullet], Seq[GameEntity[Troop]])] =
        import GameData.given
        for
          b <- entities.ofType[Bullet]
        yield
          (b, for
            e <- entities.ofType[Troop]
            if b.entity checkCollisionWith e.entity
          yield e)