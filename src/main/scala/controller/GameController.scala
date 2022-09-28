package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import controller.GameLoop.GameLoopActor
import controller.GameLoop.GameLoopCommands.GameLoopCommand
import GameLoop.GameLoopCommands.*
import controller.WaveController.WaveControllerActor
import controller.WaveController.WaveControllerCommands.GenerateWave
import model.entities.Enemy
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
    case class NewGame() extends GameControllerCommand
    case class StartGame() extends GameControllerCommand
    case class FinishGame() extends GameControllerCommand
    case class NewTurretAdded[T<:Turret](turret: T) extends GameControllerCommand
    case class NewEnemiesWave[E<:Enemy](wave: List[E]) extends GameControllerCommand

  object GameControllerActor:
    var waveNumber = 1
    var enemiesWave: Option[List[Enemy]] = None

    import GameControllerCommands.*

    def apply(gameLoop: ActorRef[Command],
              waveController: ActorRef[Command]): Behavior[Command] =
      Behaviors.setup { _ => GameControllerActor(gameLoop, waveController).standardBehavior() }

    private case class GameControllerActor(gameLoop: ActorRef[Command],
                                           waveController: ActorRef[Command]) extends Controller:
      override def standardBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
        msg match
          case NewGame() =>
            waveController ! GenerateWave(waveNumber, enemiesNumber = 2, ctx.self)
            Behaviors.same
          case NewEnemiesWave(wave) => enemiesWave = Some(wave); Behaviors.same
          case StartGame() =>
            // we will put in gameLoop object the model and the view
            // lift up game field
            gameLoop ! Start(enemiesWave.get)
            Behaviors.same
          case NewTurretAdded(turret) => ???
          case FinishGame() => ???
      })



