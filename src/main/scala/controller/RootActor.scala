package controller

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.badlogic.gdx.ScreenAdapter
import controller.GameLoopActor.*
import controller.GameLoopActor.GameLoopCommands.StartGame
import controller.RootActor.RootCommands.*
import view.{Game, GameScreen}

object RootActor:

  def apply(): Behavior[RootCommand] =
    Behaviors.setup { _ => RootActor().standardBehavior() }

  private case class RootActor():
    def standardBehavior(): Behavior[RootCommand] = Behaviors.receive((ctx, msg) => {
      msg match
        case Start(gameScreen) =>
          val view = ctx.spawnAnonymous(ViewActor(gameScreen))
          val gameLoop = ctx.spawnAnonymous(GameLoopActor(view))
          gameLoop ! StartGame()
          Behaviors.same
    })

  import RootCommands.*

  object RootCommands:
    trait RootCommand
    case class Start(gameScreen: GameScreen) extends RootCommand
