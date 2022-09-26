package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*

import scala.concurrent.duration.FiniteDuration

object GameLoop:
  object GameLoopCommands:
    sealed trait GameLoopCommand extends Command

    case class Update() extends GameLoopCommand

    case class Start() extends GameLoopCommand

    case class Stop() extends GameLoopCommand

  object GameLoopActor:

    import GameLoopCommands.*

    def apply(): Behavior[GameLoopCommand] =
      Behaviors.setup{ctx => Behaviors.withTimers { timer => GameLoopActor(ctx, timer).standardBehavior() }}

    case class GameLoopActor(ctx: ActorContext[GameLoopCommand], timer: TimerScheduler[GameLoopCommand]):
      def standardBehavior(): Behavior[GameLoopCommand] = Behaviors.receiveMessage(msg => {
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
          case Stop() => ???
      })

