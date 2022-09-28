package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import controller.GameController.GameControllerCommands.NewEnemiesWave
import model.entities.Zombie


object WaveController:

  object WaveControllerCommands:
    sealed trait WaveControllerCommand extends Command
    case class GenerateWave(waveNumber: Int, enemiesNumber: Int, replyTo: ActorRef[Command]) extends WaveControllerCommand
    case class WaveStatus(replyTo: ActorRef[Command]) extends WaveControllerCommand

  object WaveControllerActor:

    import WaveControllerCommands.*

    def apply(): Behavior[Command] =
      Behaviors.setup { _ => WaveControllerActor().standardBehavior() }

    private case class WaveControllerActor() extends Controller:
      override def standardBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
        msg match
          case GenerateWave(waveNumber, enemiesNumber, replyTo) =>
            // for the moment this class create only similar zombies next with Prolog
            replyTo ! NewEnemiesWave(List.fill(enemiesNumber)(Zombie()))
            Behaviors.same
          case WaveStatus(replyTo) => ???
      })

