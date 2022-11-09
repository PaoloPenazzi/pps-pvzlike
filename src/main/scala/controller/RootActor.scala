package controller

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.badlogic.gdx.ScreenAdapter
import controller.GameLoopActor.*
import controller.GameLoopActor.GameLoopCommands.StartGame
import controller.RootActor.RootCommands
import controller.RootActor.RootCommands.*
import view.{Game, GameScreen}

/** It's the actor responsible for launching the system.
 *
 * Sends [[StartGame]] messages to the [[GameLoop]].
 * */
object RootActor:

  /** Creates the RootActor.
   *
   * @return the actual [[Behavior]] of the actor.
   */
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

  /** The messages that [[RootActor]] can handle. */
  object RootCommands:
    trait RootCommand

    case class Start(gameScreen: GameScreen) extends RootCommand
