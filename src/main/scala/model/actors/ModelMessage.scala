package model.actors

import akka.actor.typed.ActorRef
import controller.GameLoopActor.GameLoopCommands.GameLoopCommand
import model.entities.Entity

trait ModelMessage
case class Update(timeElapsed: Double, entities: List[Entity], replyTo: ActorRef[GameLoopCommand]) extends ModelMessage
case class Collision(entity: Entity) extends ModelMessage