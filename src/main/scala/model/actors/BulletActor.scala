package model.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import model.entities.{Bullet, Enemy}

trait BulletMessages extends CommonMessages
case class Collision(enemy: Enemy) extends BulletMessages


object BulletActor:
  def apply(bullet: Bullet): Behavior[BulletMessages] =
    moving(bullet)

  def moving(bullet: Bullet): Behavior[BulletMessages] =
    Behaviors.receiveMessage(msg => {
      msg match
        case Update(timeElapsed, _, replyTo) =>
          bullet.position = (bullet.position._1 + (timeElapsed * bullet.velocity).toInt, bullet.position._2)
          // notify position change to controller
          Behaviors.same

        case Collision(enemy: Enemy) => ???

        case _ => Behaviors.same
    })
