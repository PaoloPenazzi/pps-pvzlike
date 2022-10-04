package controller

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.badlogic.gdx.backends.lwjgl3.*
import view.Game

object RootActor:

  object RootCommands:
    sealed trait RootCommand extends Command

    case class StartGame() extends RootCommand

  def apply(): Behavior[Command] =
    Behaviors.setup { _ => RootActor().standardBehavior() }

  import RootCommands.*

  private case class RootActor() extends Controller:
    override def standardBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case StartGame() =>
          val config = Lwjgl3ApplicationConfiguration()
          config.setTitle("PVZ")
          config.setResizable(true)
          config.setWindowedMode(960, 540)
          Lwjgl3Application(Game, config)
          
          val view = ctx.spawnAnonymous(ViewActor(Game.gameScreen))
          ctx.spawnAnonymous(GameLoopActor(view))
          Behaviors.same
    })
