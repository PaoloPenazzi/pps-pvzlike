package model.actors

import akka.actor.typed.ActorRef
import controller.Command
import model.entities.{Bullet, Entity}

import scala.concurrent.duration.FiniteDuration

trait ModelMessage
case class Update(timeElapsed: FiniteDuration, entities: List[Entity], replyTo: ActorRef[Command]) extends ModelMessage
case class Collision(entity: Entity, replyTo: ActorRef[Command]) extends ModelMessage
case class Shoot(replyTo: ActorRef[Command]) extends ModelMessage