package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import controller.GameLoopActor.GameLoopCommands.{StartLoop, UpdateLoop}
import model.actors.*
import model.common.Utilities.{MetaData, Sun, Velocity}
import model.entities.*
import model.{Generator, WaveGenerator}

import scala.collection.immutable.Seq
import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

import scala.language.implicitConversions
import WorldSpace.given
object GameLoopActor:

  val waveGenerator: WaveGenerator = Generator()

  import GameLoopCommands.*

  def apply(viewActor: ActorRef[ViewMessage],
            entities: Seq[(ActorRef[ModelMessage], Entity)] = List.empty,
            metaData: MetaData = MetaData()): Behavior[Command] = GameLoop(viewActor, entities, metaData).standardBehavior

  private case class GameLoop(viewActor: ActorRef[ViewMessage],
                           entities: Seq[(ActorRef[ModelMessage], Entity)],
                           metaData: MetaData) extends Controller with PauseAbility:

    override def standardBehavior: Behavior[Command] =
      Behaviors.withTimers(timer =>
        Behaviors.receive((ctx, msg) => {
          msg match
            case StartLoop() =>
              startTimer(timer, UpdateLoop())
              startTimer(timer, UpdateResources(), FiniteDuration(3, "seconds"))
              GameLoopActor(viewActor, entities, metaData)

            case StopLoop() => Behaviors.stopped

            case PauseLoop() => pauseBehavior

            case UpdateResources() =>
              startTimer(timer, UpdateResources(), FiniteDuration(3, "seconds"))
              GameLoopActor(viewActor, entities, metaData + Sun.Normal.value)

            case ChangeVelocity(velocity) => GameLoopActor(viewActor, entities, metaData >>> velocity)

            case UpdateLoop() =>
              detectCollision foreach { e => e._1._1 ! Collision(e._2._2, ctx.self); e._2._1 ! Collision(e._1._2, ctx.self);
              println("Collision between: " + e._2._2 + " AND " + e._1._2)}
              updateAll(ctx, detectInterest)
              val newWave = if isWaveOver then createWave(ctx) else List.empty
              startTimer(timer, UpdateLoop())
              GameLoopActor(viewActor, newWave ++ entities, metaData)

            case EntityUpdated(ref, entity) =>
              val newEntities = entities collect { case x if x._1 == ref => (ref, entity) case x => x}
              render(ctx, newEntities.map(_._2).toList)
              GameLoopActor(viewActor, newEntities, metaData)

            case BulletSpawned(ref, bullet) =>
              GameLoopActor(viewActor, entities :+ (ref, bullet), metaData)

            case PlacePlant(plant) =>
              plant match
                case plant if metaData.sun < plant.cost => GameLoopActor(viewActor, entities, metaData)
                case _ =>
                  GameLoopActor(viewActor, entities :+ (ctx.spawnAnonymous(TroopActor(plant)), plant), metaData - plant.cost)

            case EntityDead(ref) =>
              GameLoopActor(viewActor, entities filter { _._1 != ref }, metaData)

            case _ => Behaviors.same
        }))

    override def pauseBehavior: Behavior[Command] =
      Behaviors.receive((ctx, msg) => {
        msg match
          case StopLoop() => Behaviors.stopped

          case ResumeLoop() =>
            ctx.self ! UpdateLoop()
            GameLoopActor(viewActor, entities, metaData)

          case _ => Behaviors.same
      })

    private def startTimer(timer: TimerScheduler[Command], msg: Command, time: FiniteDuration = metaData.velocity.speed): Unit = timer.startSingleTimer(msg, time)

    private def createWave(ctx: ActorContext[Command]) =
      waveGenerator.generateNextWave.enemies.map(e => (ctx.spawnAnonymous(TroopActor(e)), e))

    private def detectCollision =
      for
        e1 <- entities
        e2 <- entities
        if e1 != e2
        if e1._2.isInstanceOf[Bullet]
        if e1._2.asInstanceOf[Bullet] checkCollisionWith e2._2
      yield (e1, e2)

    private def detectInterest =
      for
        e1 <- entities
      yield
        (e1._1, for
          e2 <- entities
          if e1 != e2
          if e1._2 isInterestedIn e2._2
        yield e2._2)

    private def isWaveOver: Boolean = entities map (_._2) collect { case enemy: Enemy => enemy } isEmpty

    private def updateAll(ctx: ActorContext[Command], interests: Seq[(ActorRef[ModelMessage], Seq[Entity])]): Unit =
      interests.foreach(e => e._1 ! Update(metaData.velocity.speed, e._2.toList, ctx.self))

    private def render(ctx: ActorContext[Command], renderedEntities: List[Entity]): Unit = viewActor ! Render(renderedEntities, ctx.self, metaData)

  object GameLoopCommands:
    sealed trait GameLoopCommand extends Command

    case class StartLoop() extends GameLoopCommand

    case class StopLoop() extends GameLoopCommand

    case class PauseLoop() extends GameLoopCommand

    case class ResumeLoop() extends GameLoopCommand

    case class UpdateLoop() extends GameLoopCommand

    case class UpdateResources() extends GameLoopCommand

    case class ChangeVelocity(velocity: Velocity) extends GameLoopCommand

    case class EntityDead(ref: ActorRef[ModelMessage]) extends GameLoopCommand

    case class EntityUpdated[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends GameLoopCommand

    case class BulletSpawned(ref: ActorRef[ModelMessage], bullet: Bullet) extends GameLoopCommand

    case class PlacePlant(plant: Plant) extends GameLoopCommand
    











  
  


