package model.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.GameLoopCommands.{EntityUpdate, GameLoopCommand}
import model.entities.{Bullet, Enemy}

object BulletActor:
  def apply(bullet: Bullet): Behavior[ModelMessage] =
    moving(bullet)

  def moving(bullet: Bullet): Behavior[ModelMessage] =
    Behaviors.receiveMessage(msg => {
      msg match
        case Update(timeElapsed, entities, replyTo) =>
          bullet updatePositionAfter timeElapsed
          // replyTo ! EntityUpdate(bullet)
          // notify controller
          entities.collect { case e: Enemy => e }
            .find(enemy => bullet checkCollisionAgainst enemy).foreach(e => ???)
          Behaviors.same

        case Collision(entity) => ???  

        case _ => Behaviors.same
    })
