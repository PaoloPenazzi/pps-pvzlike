package controller

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.GameLoopCommands.GameLoopCommand
import model.actors.ModelMessage
import view.View.EntityRenderer
import akka.actor.typed.ActorRef
import controller.Command
import model.entities.Entity

trait ViewMessage
case class Render(entities: List[Entity], replyTo: ActorRef[GameLoopCommand]) extends ModelMessage

object ViewActor:
  def apply(renderer: EntityRenderer): Behavior[ModelMessage] =
    Behaviors.receive((ctx, msg) => {
      msg match
        case Render(list, _) =>
          renderer.renderEntities(list)
          Behaviors.same
    })
