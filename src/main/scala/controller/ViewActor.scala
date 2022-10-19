package controller

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.GameLoopCommands.GameLoopCommand
import model.actors.ModelMessage
import view.View.EntityRenderer
import akka.actor.typed.ActorRef
import controller.Command
import model.entities.{Entity, MetaData}

trait ViewMessage
case class Render(entities: List[Entity], replyTo: ActorRef[GameLoopCommand], metaData: MetaData) extends ViewMessage

object ViewActor:
  def apply(renderer: EntityRenderer): Behavior[ViewMessage] =
    Behaviors.receive((ctx, msg) => {
      msg match
        case Render(list, _, _) =>
          renderer.renderEntities(list)
          Behaviors.same
    })
