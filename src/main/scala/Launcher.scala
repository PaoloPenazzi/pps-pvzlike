import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.util.Timeout
import controller.Command
import controller.Controller.ControllerActor
import controller.Controller.ControllerCommands.{ControllerCommand, StartGame, FinishGame}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

object Launcher {

  @main
  def main() =
    val controller: ActorSystem[ControllerCommand] = ActorSystem(ControllerActor(), name = "launcher")
    controller ! StartGame()

}
