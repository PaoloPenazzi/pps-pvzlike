package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import controller.GameController.GameControllerCommands.GameControllerCommand
import model.entities.Entity

import scala.concurrent.duration.FiniteDuration

/*
* Essentially the GameLoop do three things (it works as router for the packet, so it only redirect the messages):
* - Start loop
* - Update the world (both model and view but the view response to controller)
* - Stop loop
* - Eventually pause and resume the game/loop
*/
object GameLoop:
  object GameLoopCommands:
    sealed trait GameLoopCommand extends Command

    case class Update() extends GameLoopCommand

    case class Start() extends GameLoopCommand

    case class Pause() extends GameLoopCommand

    case class Resume() extends GameLoopCommand

    // the presence or not of an entity is defined by this message: if i receive i will update that entity otherwise the 
    // entity is dead and so i don't have to update that one
    case class EntityUpdate[E<:Entity](entity: E) extends GameLoopCommand

    case class Stop() extends GameLoopCommand
  
  object GameLoopActor:

    import GameLoopCommands.*

    def apply(): Behavior[Command] =
      Behaviors.setup{ _ => Behaviors.withTimers { timer => GameLoopActor(timer).standardBehavior }}

    private case class GameLoopActor(timer: TimerScheduler[Command]) extends Controller with PausableController:
      override def standardBehavior: Behavior[Command] = Behaviors.receive( (ctx, msg) => {
        msg match
          case Update() =>
            // update model
            // update view
            // find the correct time update
            timer.startSingleTimer(Update(), FiniteDuration(10, "second"))
            Behaviors.same
          case Start() =>
            // start the model
            // start the view
            timer.startSingleTimer(Update(), FiniteDuration(10, "second"))
            Behaviors.same
          case EntityUpdate(entity) =>
            // pass the model to view
            Behaviors.same
          case Pause() => pauseBehavior
          case Stop() => Behaviors.stopped
          case _ => Behaviors.same
      })

      override def pauseBehavior: Behavior[Command] = Behaviors.receive( (ctx, msg) => {
        msg match
          case Stop() => Behaviors.stopped
          case Resume() =>
            ctx.self ! Update()
            standardBehavior
          case _ => Behaviors.same
  
      })

