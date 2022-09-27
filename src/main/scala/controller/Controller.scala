package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import controller.GameLoop.GameLoopActor
import controller.GameLoop.GameLoopCommands.GameLoopCommand
import GameLoop.GameLoopCommands.*


object Controller:

  object ControllerCommands:
    sealed trait ControllerCommand extends Command
    case class StartGame() extends ControllerCommand
    case class FinishGame() extends ControllerCommand

  object ControllerActor:

    import ControllerCommands.*

    def apply(): Behavior[Command] =
      Behaviors.setup { ctx => ControllerActor(ctx).standardBehavior() }

    case class ControllerActor(ctx: ActorContext[Command]):
      def standardBehavior(): Behavior[Command] = Behaviors.receiveMessage(msg => {
        msg match
          case StartGame() =>
            // we will put in gameLoop object the model and the view
            val gameLoop = ctx.spawnAnonymous(GameLoopActor())
            gameLoop ! Start()
            Behaviors.same
          case FinishGame() => ???
      })



