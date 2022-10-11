package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import model.actors.{EnemyActor, ModelMessage, TurretActor, Update}
import model.entities.{Bullet, Enemy, Entity, Plant, Turret}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

object GameLoopActor:

  object GameLoopCommands:
    sealed trait GameLoopCommand extends Command

    case class StartLoop() extends GameLoopCommand

    case class StopLoop() extends GameLoopCommand

    case class PauseLoop() extends GameLoopCommand

    case class ResumeLoop() extends GameLoopCommand

    case class UpdateLoop() extends GameLoopCommand

    case class EntityDead[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends GameLoopCommand

    case class EntityUpdated[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends GameLoopCommand

    case class EntitySpawned[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends GameLoopCommand

    case class EntityUpgraded[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends GameLoopCommand


  // TODO from here, make it better...
  var enemiesWave: Seq[(ActorRef[ModelMessage], Enemy)] = List[(ActorRef[ModelMessage], Enemy)]()
  var bullets: Seq[(ActorRef[ModelMessage], Bullet)] = List[(ActorRef[ModelMessage], Bullet)]()
  var entities: Seq[(ActorRef[ModelMessage], Entity)] = List[(ActorRef[ModelMessage], Entity)]()

  def apply(viewActor: ActorRef[ViewMessage]): Behavior[Command] =
    Behaviors.setup { _ => Behaviors.withTimers { timer => GameLoopActor(timer, viewActor).standardBehavior() } }

  import GameLoopCommands.*

  private case class GameLoopActor(timer: TimerScheduler[Command],
                                   viewActor: ActorRef[ViewMessage]) extends Controller with PausableController:
    override def standardBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case StartLoop() =>
          entities = entities :+ (ctx.spawnAnonymous(TurretActor(Plant(1,0))), Plant(1,0))
          createWave(ctx)
          startTimer(timer)
          Behaviors.same
        case StopLoop() => Behaviors.stopped
        case PauseLoop() => pauseBehavior()
        case UpdateLoop() =>
          // detectCollision
          val interests = detectInterest
          entities.foreach(e => e._1 ! Update(FiniteDuration(16, "milliseconds"), interests.filter(_._1 == e._1).head._2.toList, ctx.self))
          startTimer(timer)
          Behaviors.same
        case EntityUpdated(ref, entity) =>
          // todo check if the wave is end
          val oldEntity = entities.filter(e => e._1 == ref).head._2
          entities = entities.updated(entities.indexOf((ref, oldEntity)), (ref, entity))
          viewActor ! Render(entities.map(e => e._2).toList, ctx.self)
          Behaviors.same
        case EntitySpawned(ref, entity) =>
          entities = entities :+ (ref, entity)
          Behaviors.same
//          entity match
//          // todo after the addiction, do we want to send the message instantly?
//          case _: Bullet => bullets = bullets :+ (ref, entity.asInstanceOf[Bullet]); Behaviors.same
//          case _: Enemy => enemiesWave = enemiesWave :+ (ref, entity.asInstanceOf[Enemy]); Behaviors.same
//          case _ => Behaviors.same

        case EntityUpgraded(ref, entity) =>
          entities = entities.updated(entities.indexOf(ref), (ref, entity))
          entity match
          // todo after the addiction, do we want to send the message instantly?
          case _: Bullet => bullets = bullets.updated(bullets.indexOf(ref), (ref, entity.asInstanceOf[Bullet])); Behaviors.same
          case _: Enemy => enemiesWave = enemiesWave.updated(enemiesWave.indexOf(ref), (ref, entity.asInstanceOf[Enemy])); Behaviors.same
          case _ => Behaviors.same

        case EntityDead(ref, entity) =>
          entities = entities.filterNot(e => e == (ref, entity))
          entity match
            // todo after the addiction, do we want to send the message instantly?
            case _: Bullet => bullets = bullets.filterNot(e => e == (ref, entity.asInstanceOf[Bullet])); Behaviors.same
            case _: Enemy => enemiesWave = enemiesWave.filterNot(e => e == (ref, entity.asInstanceOf[Enemy])); Behaviors.same
            case _ => Behaviors.same

        case _ => Behaviors.same
    })

    override def pauseBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case StopLoop() => Behaviors.stopped
        case ResumeLoop() =>
          ctx.self ! UpdateLoop()
          standardBehavior()
        case _ => Behaviors.same
    })

    def startTimer(timer: TimerScheduler[Command]) = timer.startSingleTimer(UpdateLoop(), FiniteDuration(16, "milliseconds"))

    def createWave(ctx: ActorContext[Command]): Unit =
      // genera una wave di zombie poi la lista di zombie creata verrà mappata in un'altra lista fatta di tuple (zombieRef, zombie)
      // e alla fine gli zombie verranno aggiunti alla lista enemiesWave lista che contiene tutti gli zombie
      val newWave = WaveSupervisor.generateWave(2).map(e => (ctx.spawnAnonymous(EnemyActor(e)), e))
      entities = entities :++ newWave

    def detectCollision = ???
      // todo provarlo a fare con for-yield o pimp-my-library
      // controlla per ogni bullet nel gioco se questo ha colpito qualche entità (che non sia lui). Se il bullet ha colpito qualcuno (quindi
      // la lista entitiesColl non è vuota) allora mando un messaggio di collisione ad ogni entità e al bullet in questione
       /*bullets.foreach(b =>
         val entitiesColl = entities.filter(e => (b._1 != e._1) && b._2.collideWith(e._2))
         if entitiesColl.nonEmpty then entities.foreach(e => {e._1 ! CollisionWith(b._2); b._1 ! CollisionWith(e._2)}))*/


    def detectInterest: Seq[(ActorRef[ModelMessage], Seq[Entity])] =
      for
        e1 <- entities
      yield
        (e1._1, for
          e2 <- entities
          if e1._1 != e2._1
          if e1._2.interest(e2._2)
        yield e2._2)

    def updateAll(interestForAll: Seq[(ActorRef[ModelMessage], Seq[Entity])]) = ???
      // mando un messaggio di update ad ogni entità e gli allego le entità a cui è interessato
      // interestForAll.foreach(e => e._1 ! UpdateModel(e._2.map( x => x._2)))







  
  


