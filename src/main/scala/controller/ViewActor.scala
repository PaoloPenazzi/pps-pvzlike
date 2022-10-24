package controller

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import controller.Command
import controller.GameLoopActor.GameLoopCommands.{GameLoopCommand, PlacePlant}
import model.actors.ModelMessage
import model.common.Utilities.MetaData
import model.entities.{Entity, Plant}
import view.View.EntityRenderer

trait ViewMessage
case class Render(entities: List[Entity], replyTo: ActorRef[Command], metaData: MetaData) extends ViewMessage

object ViewActor:
  var gameLoopActor: Option[ActorRef[Command]] = None
  def sendPlacePlant(plant: Plant): Unit =
    gameLoopActor.foreach(_ ! PlacePlant(plant))

  def apply(renderer: EntityRenderer): Behavior[ViewMessage] =
    Behaviors.receive((ctx, msg) => {
      msg match
        case Render(list, replyTo, metaData) =>
          renderer.renderEntities(list)
          renderer.renderMetadata(metaData)
          gameLoopActor = Some(replyTo)
          Behaviors.same
    })
