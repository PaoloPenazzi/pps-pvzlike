package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import controller.GameLoopActor.GameLoopCommands.{StartLoop, UpdateLoop}
import model.{Generator, WaveGenerator}
import model.actors.{Collision, EnemyActor, ModelMessage, TurretActor, Update}
import model.entities.{Bullet, Enemy, Entity, Plant, Turret}

import scala.collection.immutable.Seq
import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

object GameLoopActor:

  object GameLoopCommands:
    sealed trait GameLoopCommand extends Command

    case class StartLoop() extends GameLoopCommand

    case class StopLoop() extends GameLoopCommand

    case class PauseLoop() extends GameLoopCommand

    case class ResumeLoop() extends GameLoopCommand

    case class UpdateLoop() extends GameLoopCommand

    case class EntityDead[E <: Entity](ref: ActorRef[ModelMessage]) extends GameLoopCommand

    case class EntityUpdated[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends GameLoopCommand

    case class EntitySpawned[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends GameLoopCommand

  import GameLoopCommands.*

  val waveGenerator: WaveGenerator = Generator()
  val updateTime: FiniteDuration = FiniteDuration(16, "milliseconds")

  def apply(viewActor: ActorRef[ViewMessage],
            entities: Seq[(ActorRef[ModelMessage], Entity)] = List.empty): Behavior[Command] =
    standardBehavior(viewActor, entities)

  def standardBehavior(viewActor: ActorRef[ViewMessage],
                       entities: Seq[(ActorRef[ModelMessage], Entity)]): Behavior[Command] =
      Behaviors.withTimers( timer =>
        Behaviors.receive((ctx, msg) => {
          msg match
            case StartLoop() =>
              startTimer(timer)
              GameLoopActor(viewActor, createWave(ctx))

            case StopLoop() => Behaviors.stopped

            case PauseLoop() => pauseBehavior(viewActor, entities)

            case UpdateLoop() =>
              detectCollision(entities).foreach{e => e._1._1 ! Collision(e._2._2, ctx.self); e._2._1 ! Collision(e._1._2, ctx.self)}
              updateAll(ctx, detectInterest(entities))
              val newWave: List[(ActorRef[ModelMessage], Entity)] = if isWaveOver(entities) then createWave(ctx) else List.empty
              startTimer(timer)
              GameLoopActor(viewActor, newWave ++ entities)

            case EntityUpdated(ref, entity) =>
              val newEntities = entities collect {case x if x._1 == ref => (ref, entity)}
              render(ctx, viewActor, newEntities.map(_._2))
              GameLoopActor(viewActor, newEntities)

            case EntitySpawned(ref, entity) =>
              GameLoopActor(viewActor, entities :+ (ref, entity))

            case EntityDead(ref) =>
              GameLoopActor(viewActor, entities filter {e => e._1 != ref})

            case _ => Behaviors.same
    }))

  def pauseBehavior(viewActor: ActorRef[ViewMessage],
                    entities: Seq[(ActorRef[ModelMessage], Entity)]): Behavior[Command] =
    Behaviors.receive((ctx, msg) => {
      msg match
        case StopLoop() => Behaviors.stopped

        case ResumeLoop() =>
          ctx.self ! UpdateLoop()
          GameLoopActor(viewActor, entities)

        case _ => Behaviors.same
    })


  private def startTimer(timer: TimerScheduler[Command]): Unit = timer.startSingleTimer(UpdateLoop(), updateTime)

  private def createWave(ctx: ActorContext[Command]) =
    waveGenerator.generateNextWave.enemies.map(e => (ctx.spawnAnonymous(EnemyActor(e)), e))

  private def detectCollision(entities: Seq[(ActorRef[ModelMessage], Entity)]) =
    for
      e1 <- entities
      e2 <- entities
      if e1._2.isInstanceOf[Bullet]
      if e1._1 != e2._1
      // if e1._2.collideWith(e2._2)
    yield (e1, e2)

  private def detectInterest(entities: Seq[(ActorRef[ModelMessage], Entity)]) =
    for
      e1 <- entities
    yield
      (e1._1, for
        e2 <- entities
        if e1._1 != e2._1
        if e1._2 filter e2._2
      yield e2._2)

  private def isWaveOver(entities: Seq[(ActorRef[ModelMessage], Entity)]) =
    entities map(_._2) collect{ case enemy: Enemy => enemy } isEmpty

  private def updateAll(ctx: ActorContext[Command], interests: Seq[(ActorRef[ModelMessage], Seq[Entity])]) =
    interests.foreach(e => e._1 ! Update(updateTime, e._2.toList, ctx.self))

  private def render(ctx: ActorContext[Command], viewActor: ActorRef[ViewMessage], entities: Seq[Entity]) =
    viewActor ! Render(entities.toList, ctx.self)











  
  


