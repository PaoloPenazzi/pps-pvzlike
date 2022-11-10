package model.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.actors.GameLoopActor.GameLoopCommands.{Command, EntityDead, EntityUpdated}
import model.entities.{Bullet, Zombie}

/** It's the the actor that defines the behavior of the bullet.
 *
 * Sends [[ModelMessage]] messages to the GameLoopActor.
 * */
object BulletActor:
  def apply(bullet: Bullet): Behavior[ModelMessage] =
    moving(bullet)

  /** Provides the normal behavior of the BulletActor.
   *
   * @return a new updated BulletActor instance with the same [[Behavior]].
   */
  def moving(bullet: Bullet): Behavior[ModelMessage] =
    Behaviors.receive((ctx, msg) => {
      msg match
        case Update(timeElapsed, interests, replyTo) =>
          val updatedBullet = bullet.update(timeElapsed, interests)
          replyTo ! EntityUpdated(ctx.self, updatedBullet)
          BulletActor(updatedBullet)

        case Collision(entity, replyTo) =>
          if bullet shouldDisappearAfterHitting entity
          then
            replyTo ! EntityDead(ctx.self, Some(bullet))
            Behaviors.stopped
          Behaviors.same

        case _ => Behaviors.same
    })
