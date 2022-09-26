package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}


object Controller:

  object ControllerCommands:
    sealed trait ControllerCommand

    case class NewGame() extends ControllerCommand

    case class StartGame() extends ControllerCommand

    case class FinishGame() extends ControllerCommand

  object ControllerActor:

    import ControllerCommands.*

    def apply(): Behavior[ControllerCommand] =
      Behaviors.setup { ctx => ControllerActor(ctx).normalBehavior() }

    case class ControllerActor(ctx: ActorContext[ControllerCommand]):
      def normalBehavior(): Behavior[ControllerCommand] = Behaviors.receiveMessage(msg => {
        msg match
          case NewGame() => ???
          case StartGame() => ???
          case FinishGame() => ???
      })



