package model.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.GameLoopCommands.{EntityUpdate, GameLoopCommand, EntityDead}
import model.entities.{Bullet, Enemy}

object BulletActor:
  def apply(bullet: Bullet): Behavior[ModelMessage] =
    moving(bullet)

  def moving(bullet: Bullet): Behavior[ModelMessage] =
    Behaviors.receive( (ctx,msg) => {
      msg match
        case Update(timeElapsed, interests, replyTo) =>
          val updatedBullet = bullet.update(timeElapsed, interests)
          replyTo ! EntityUpdate(ctx.self, updatedBullet)
          BulletActor(updatedBullet)

        case Collision(entity, replyTo) =>
          if bullet shouldDisappearAfterHitting entity
          then
            replyTo ! EntityDead(ctx.self, bullet)
            Behaviors.stopped
          Behaviors.same

        case _ => Behaviors.same
    })
