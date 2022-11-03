import controller.RootActor
import controller.RootActor.RootCommands.StartGame
import akka.actor.typed.ActorSystem
import com.badlogic.gdx.backends.lwjgl3.*
import view.Game

object Launcher :
  @main
  def main(): Unit =
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("PVZ")
    config.setResizable(false)
    config.setWindowedMode(960, 540)
    Lwjgl3Application(Game, config)


