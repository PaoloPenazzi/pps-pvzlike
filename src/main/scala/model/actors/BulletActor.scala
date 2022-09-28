package model.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoop.GameLoopCommands.{EntityUpdate, GameLoopCommand}
import model.entities.{Bullet, Enemy}

trait BulletMessages extends CommonMessages
case class Collision(enemy: List[Enemy], replyTo: ActorRef[CommonMessages]) extends BulletMessages

object BulletActor:
  def apply(bullet: Bullet): Behavior[BulletMessages | GameLoopCommand] =
    moving(bullet)

  def moving(bullet: Bullet): Behavior[BulletMessages | GameLoopCommand] =
    Behaviors.receiveMessage(msg => {
      msg match
        case Update(timeElapsed, entities, replyTo) =>
          bullet updatePositionAfter timeElapsed
          //replyTo ! EntityUpdate(bullet)
          // notify controller
          entities.collect { case e: Enemy => e }
            .find(enemy => bullet checkCollisionAgainst enemy).foreach(e => ???)
          Behaviors.same

        case _ => Behaviors.same
    })
