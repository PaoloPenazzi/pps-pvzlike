package controller.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import controller.ViewActor
import controller.actors.GameLoopActor.GameLoop
import controller.actors.GameLoopActor.GameLoopCommands.StartGame
import controller.actors.RootActor.RootCommands.{RootCommand, Start}

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
        case Start() =>
          val view = ctx.spawnAnonymous(ViewActor())
          val gameLoop = ctx.spawnAnonymous(GameLoopActor(view))
          gameLoop ! StartGame()
          Behaviors.same
    })

  /** The messages that [[RootActor]] can handle. */
  object RootCommands:
    trait RootCommand

    case class Start() extends RootCommand
