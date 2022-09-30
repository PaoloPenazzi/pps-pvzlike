package controller

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object RootActor:

  object RootCommands:
    sealed trait RootCommand extends Command

    case class StartView() extends RootCommand

  def apply(): Behavior[Command] =
    Behaviors.setup { _ => RootActor().standardBehavior() }

  import RootCommands.*
  
  private case class RootActor():
    def standardBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case StartView() => ???
    })
