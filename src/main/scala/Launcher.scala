import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler, Terminated}
import akka.util.Timeout
import controller.Command
import controller.GameController.GameControllerActor
import controller.GameController.GameControllerCommands.{FinishGame, GameControllerCommand, NewGame, StartGame}
import controller.GameLoop.GameLoopActor
import controller.WaveController.WaveControllerActor

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success


object Launcher {

  // to rebuild...
  @main
  def main(): Unit =
    val system = ActorSystem(
      Behaviors.setup[Any] { ctx =>
      val gameLoop = ctx.spawnAnonymous(GameLoopActor())
      val waveController = ctx.spawnAnonymous(WaveControllerActor())
      Behaviors.receiveMessage[Any] { _ =>
        val gameController = ctx.spawnAnonymous(GameControllerActor(gameLoop, waveController))
        ctx.watch(gameController)
        gameController ! NewGame()
        Behaviors.same
      }.receiveSignal { case (_, _ @Terminated(_)) =>
        System.out.println("PVZ terminated. Shutting down")
        Behaviors.stopped
      }
    }, "main")
    system ! StartGame()

}
