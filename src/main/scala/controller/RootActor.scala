package controller

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.badlogic.gdx.ScreenAdapter
import controller.GameLoopActor.*
import controller.GameLoopActor.GameLoopCommands.StartGame
import view.{Game, GameScreen}

object RootActor:

  object RootCommands:
    sealed trait RootCommand extends Command

    case class Start(gameScreen: GameScreen) extends RootCommand

  def apply(): Behavior[Command] =
    Behaviors.setup { _ => RootActor().standardBehavior }

  import RootCommands.*

  private case class RootActor() extends Controller:
    override def standardBehavior: Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case Start(gameScreen) =>
          val view = ctx.spawnAnonymous(ViewActor(gameScreen))
          val gameLoop = ctx.spawnAnonymous(GameLoopActor(view))
          gameLoop ! StartGame()
          Behaviors.same
    })
