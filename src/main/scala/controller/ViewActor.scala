package controller

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import controller.Command
import controller.GameLoopActor.GameLoopCommands.GameLoopCommand
import model.actors.ModelMessage
import model.common.Utilities.MetaData
import model.entities.Entity
import view.View.EntityRenderer

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
