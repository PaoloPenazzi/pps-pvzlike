package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import model.entities.Turrets.Turret
import model.entities.{Bullet, Enemy, Entity}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

object GameLoopActor:

  object GameLoopCommands:
    sealed trait GameLoopCommand extends Command

    case class StartLoop() extends GameLoopCommand

    case class Update() extends GameLoopCommand

    case class Start[E <: Enemy](wave: List[E]) extends GameLoopCommand

    case class Pause() extends GameLoopCommand

    case class Resume() extends GameLoopCommand

    case class StartGame() extends GameLoopCommand

    case class FinishGame() extends GameLoopCommand

    case class NewTurretAdded[T <: Turret](turret: T) extends GameLoopCommand

    case class NewEnemiesWave[E <: Enemy](wave: List[E]) extends GameLoopCommand

    // the presence or not of an entity is defined by this message: if i receive i will update that entity otherwise the
    // entity is dead and so i don't have to update that one
    case class EntityUpdate[E <: Entity](entity: E) extends GameLoopCommand

    case class Stop() extends GameLoopCommand

  // TODO from here, make it better...
  var enemiesWave = mutable.HashMap[ActorRef[Enemy], Enemy]()
  var bullets = mutable.HashMap[ActorRef[Enemy], Enemy]()
  var entities = mutable.HashMap[ActorRef[Enemy], Enemy]()

  def apply(): Behavior[Command] =
    Behaviors.setup { _ => Behaviors.withTimers { timer => GameLoopActor(timer).standardBehavior() } }

  import GameLoopCommands.*

  private case class GameLoopActor(timer: TimerScheduler[Command]) extends Controller with PausableController:
    override def standardBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case StartLoop() =>
          createWave(ctx)
          startTimer(timer)
          Behaviors.same
        case Update() =>
          // update model
          // update view
          // find the correct time update
          //wave.foreach(enemy ! update)
          detectCollision()
          detectInterest()
          entities.foreach(_)
          timer.startSingleTimer(Update(), FiniteDuration(10, "second"))
          Behaviors.same
        case Start(wave) =>
          // model ! start
          // view ! start
          // enemiesWave = Some(wave)
          timer.startSingleTimer(Update(), FiniteDuration(10, "second"))
          Behaviors.same
        case EntityUpdate(entity) =>
          // pass the model to view
          Behaviors.same
        case Pause() => pauseBehavior()
        case Stop() => Behaviors.stopped
        case _ => Behaviors.same
    })

    override def pauseBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case Stop() => Behaviors.stopped
        case Resume() =>
          ctx.self ! Update()
          standardBehavior()
        case _ => Behaviors.same
    })

    def startTimer(timer: TimerScheduler[Command]) = timer.startSingleTimer(Update(), FiniteDuration(10, "second"))

    def createWave(ctx: ActorContext[Command]) = ???
      // WaveSupervisor.generateWave(2).map( e => (ctx.spawnAnonymous(EnemyActor(enemy)), e)).foreach(t => enemiesWave + t)

    def detectCollision() = ???

    def detectInterest() = ???







  
  


