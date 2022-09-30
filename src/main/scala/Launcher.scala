import controller.RootActor
import controller.RootActor.RootCommands.StartGame
import akka.actor.typed.ActorSystem

object Launcher :
  @main
  def main(): Unit =
    val system = ActorSystem(RootActor(), "launcher")
    system ! StartGame()
