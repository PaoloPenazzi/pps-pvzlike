package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import controller.GameLoopActor.GameLoopCommands.{StartLoop, UpdateLoop}
import model.GameData
import model.GameData.{GameEntity, GameSeq}
import model.actors.*
import model.common.Utilities.{MetaData, Sun, Velocity}
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

  def apply(viewActor: ActorRef[ViewMessage],
            entities: GameSeq = GameSeq(Seq.empty),
            metaData: MetaData = MetaData()): Behavior[Command] = GameLoop(viewActor, entities, metaData).standardBehavior

  private case class GameLoop(viewActor: ActorRef[ViewMessage],
                              entities: GameSeq,
                              metaData: MetaData) extends Controller with PauseAbility :

    override def standardBehavior: Behavior[Command] =
      Behaviors.withTimers(timer =>
        Behaviors.receive((ctx, msg) => {
          msg match
            case StartLoop() =>
              waveGenerator.resetWaves()
              startTimer(timer, UpdateLoop())
              startTimer(timer, UpdateResources(), resourceTimer)
              Behaviors.same

            case EndGame() =>
              Game.endGame()
              Behaviors.stopped

            case PauseLoop() => pauseBehavior

            case UpdateResources() =>
              startTimer(timer, UpdateResources(), resourceTimer)
              GameLoopActor(viewActor, entities, metaData + Sun.Normal.value)

            case ChangeVelocity(velocity) => GameLoopActor(viewActor, entities, metaData >>> velocity)

            case UpdateLoop() =>
              checkCollision(entities, ctx)
              updateAll(ctx, metaData.velocity, detectInterest(entities))
              val newWave = if isWaveOver(entities) then createWave(ctx) else List.empty
              startTimer(timer, UpdateLoop())
              GameLoopActor(viewActor, GameSeq(newWave ++ entities.seq), metaData)

            case EntityUpdated(ref, entity) =>
              val newEntities = entities.seq collect { case x if x.ref == ref => GameEntity(ref, entity) case x => x }
              render(ctx, viewActor, metaData, newEntities.map(_.entity).toList)
              GameLoopActor(viewActor, GameSeq(newEntities), metaData)

            case BulletSpawned(ref, bullet) =>
              GameLoopActor(viewActor, GameSeq(entities.seq :+ GameEntity(ref, bullet)), metaData)

            case PlacePlant(troop) =>
              troop.asInstanceOf[Plant] match
                case plant if metaData.sun < plant.cost => GameLoopActor(viewActor, entities, metaData)
                case _ =>
                  val newGameSeq = GameSeq(entities.seq :+ GameEntity(ctx.spawnAnonymous(TroopActor(troop)), troop))
                  val newMetaData = metaData - troop.asInstanceOf[Plant].cost
                  GameLoopActor(viewActor, newGameSeq, newMetaData)

            case EntityDead(ref) => GameLoopActor(viewActor, GameSeq(entities.seq filter { _.ref != ref }), metaData)

            case _ => Behaviors.same
        }))

    override def pauseBehavior: Behavior[Command] =
      Behaviors.receive((ctx, msg) => {
        msg match
          case ResumeLoop() =>
            ctx.self ! UpdateLoop()
            GameLoopActor(viewActor, entities, metaData)

          case _ => Behaviors.same
      })

  object GameLoopCommands:
    sealed trait GameLoopCommand extends Command
    case class StartLoop() extends GameLoopCommand
    case class PauseLoop() extends GameLoopCommand
    case class ResumeLoop() extends GameLoopCommand
    case class UpdateLoop() extends GameLoopCommand
    case class EndGame() extends GameLoopCommand
    case class UpdateResources() extends GameLoopCommand
    case class ChangeVelocity(velocity: Velocity) extends GameLoopCommand
    case class EntityDead(ref: ActorRef[ModelMessage]) extends GameLoopCommand
    case class EntityUpdated[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends GameLoopCommand
    case class BulletSpawned(ref: ActorRef[ModelMessage], bullet: Bullet) extends GameLoopCommand
    case class PlacePlant(troop: Troop) extends GameLoopCommand

  object GameLoopUtils:
    val resourceTimer: FiniteDuration = FiniteDuration(3, "seconds")

    def startTimer(timer: TimerScheduler[Command], msg: Command, time: FiniteDuration = Velocity.Normal.speed): Unit = timer.startSingleTimer(msg, time)

    def createWave(ctx: ActorContext[Command]): Seq[GameEntity[Entity]] =
      waveGenerator.generateNextWave.enemies.map(e => GameEntity(ctx.spawnAnonymous(TroopActor(e)), e))

    def detectInterest(entities: GameSeq): Seq[(ActorRef[ModelMessage], Seq[Entity])] =
      for
        e <- entities.seq
      yield
        (e.ref, for
          e2 <- entities.seq
          if e != e2
          if e.entity isInterestedIn e2.entity
        yield e2.entity)

    def isWaveOver: GameSeq => Boolean = _.seq map (_.entity) collect { case enemy: Zombie => enemy } isEmpty

    def updateAll(ctx: ActorContext[Command], velocity: Velocity, interests: Seq[(ActorRef[ModelMessage], Seq[Entity])]): Unit =
      interests foreach(e => e._1 ! Update(velocity.speed, e._2.toList, ctx.self))

    def render(ctx: ActorContext[Command], viewActor: ActorRef[ViewMessage], metaData: MetaData,
               renderedEntities: List[Entity]): Unit =
      viewActor ! Render(renderedEntities, ctx.self, metaData)

    object CollisionUtils:
      def checkCollision(entities: GameSeq, ctx: ActorContext[Command]): Unit =
        detectCollision(entities) filter (_._2.nonEmpty) foreach { e =>
          if e._1.entity hitMultipleTimes
          then e._2 foreach { r => sendCollisionMessage(e._1, r, ctx); sendCollisionMessage(r, e._1, ctx) }
          else {
            sendCollisionMessage(e._1, e._2.head, ctx);
            sendCollisionMessage(e._2.head, e._1, ctx)
          }
        }

      def sendCollisionMessage[A <: Entity, E <: Entity](to: GameEntity[A], from: GameEntity[E], ctx: ActorContext[Command]): Unit =
        to.ref ! Collision(from.entity, ctx.self)

      def detectCollision(entities: GameSeq): Seq[(GameEntity[Bullet], Seq[GameEntity[Entity]])] =
        for
          b <- entities.ofType[Bullet]
        yield
          (b, for
            e <- entities.seq
            if b != e
            if b.entity checkCollisionWith e.entity
          yield e)