package controller

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.*
import controller.GameLoopActor.GameLoopCommands.StartLoop
import view.Game

object RootActor:

  object RootCommands:
    sealed trait RootCommand extends Command

    case class StartGame() extends RootCommand

  def apply(): Behavior[Command] =
    Behaviors.setup { _ => RootActor().standardBehavior }

  import RootCommands.*

  private case class RootActor() extends Controller:
    override def standardBehavior: Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case StartGame() => 
          val view = ctx.spawnAnonymous(ViewActor(Game.gameScreen))
          val gameLoop = ctx.spawnAnonymous(GameLoopActor(view))
          gameLoop ! StartLoop()
          Behaviors.same
    })
