package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import controller.Controller.ControllerCommands.ControllerCommand
import model.entities.Entity

import scala.concurrent.duration.FiniteDuration

/*
* Essentially the GameLoop do three things (it works as router for the packet, so it only redirect the messages):
* - Start loop
* - Update the world (both model and view but the view response to controller)
* - Stop loop
* Beyond these things it can do:
* - Eventually understand if the round is finished
* - Eventually pause and resume the game/loop
*/
object GameLoop:
  object GameLoopCommands:
    sealed trait GameLoopCommand extends Command

    case class Update() extends GameLoopCommand

    case class Start() extends GameLoopCommand

    // the presence or not of an entity is defined by this message: if i receive i will update that entity otherwise the 
    // entity is dead and so i don't have to update that one
    case class EntityUpdate[E<:Entity](entity: E) extends GameLoopCommand

    case class Stop() extends GameLoopCommand
  
  object GameLoopActor:

    import GameLoopCommands.*

    def apply(): Behavior[Command] =
      Behaviors.setup{ctx => Behaviors.withTimers { timer => GameLoopActor(ctx, timer).standardBehavior() }}

    case class GameLoopActor(ctx: ActorContext[Command], timer: TimerScheduler[Command]):
      def standardBehavior(): Behavior[Command] = Behaviors.receiveMessage(msg => {
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
          case Stop() => Behaviors.stopped
      })

