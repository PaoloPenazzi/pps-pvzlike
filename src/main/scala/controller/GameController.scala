package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import controller.GameLoop.GameLoopActor
import controller.GameLoop.GameLoopCommands.GameLoopCommand
import GameLoop.GameLoopCommands.*
import model.entities.Turrets.Turret

/*
* Essentially the Controller is the controller of all games so for each important phase of a match it will something:
* - Start the game
* - Start next round
* - Handle all response from model/view
*/
object GameController:

  object GameControllerCommands:
    sealed trait GameControllerCommand extends Command
    case class StartGame() extends GameControllerCommand
    case class FinishGame() extends GameControllerCommand
    case class NewTurretAdded[T<:Turret](turret: T) extends GameControllerCommand

  object GameControllerActor:

    import GameControllerCommands.*

    def apply(): Behavior[Command] =
      Behaviors.setup { ctx => GameControllerActor(ctx).standardBehavior() }

    private case class GameControllerActor(ctx: ActorContext[Command]):
      def standardBehavior(): Behavior[Command] = Behaviors.receiveMessage(msg => {
        msg match
          case StartGame() =>
            // we will put in gameLoop object the model and the view
            // lift up game field
            val gameLoop = ctx.spawnAnonymous(GameLoopActor())
            gameLoop ! Start()
            Behaviors.same
          case NewTurretAdded(turret) => ???
          case FinishGame() => ???
      })



