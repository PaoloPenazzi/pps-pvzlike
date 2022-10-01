package model.actors

import akka.actor.typed.ActorRef
import controller.Command
import model.entities.{Bullet, Entity}

trait ModelMessage
case class Update(timeElapsed: Double, entities: List[Entity], replyTo: ActorRef[Command]) extends ModelMessage
case class Collision(entity: Entity, replyTo: ActorRef[Command]) extends ModelMessage
case class Shoot(replyTo: ActorRef[Command]) extends ModelMessage


// todo va nel controller
case class EntitySpawned(bullet: Bullet, actorRef: ActorRef[ModelMessage]) extends Command
case class EntityDead(entity: Entity, actorRef: ActorRef[ModelMessage]) extends Command