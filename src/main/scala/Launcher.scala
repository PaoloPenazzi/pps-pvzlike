import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.util.Timeout
import controller.Command
import controller.GameController.GameControllerActor
import controller.GameController.GameControllerCommands.{GameControllerCommand, StartGame, FinishGame}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

object Launcher {

  @main
  def main() =
    val controller: ActorSystem[GameControllerCommand] = ActorSystem(GameControllerActor(), name = "launcher")
    controller ! StartGame()

}
