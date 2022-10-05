package model.actors

import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.GameLoopCommands.EntityUpdated
import model.entities.Enemy
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object EnemyActor :
  def apply(enemy: Enemy): Behavior[ModelMessage] =
    moving(enemy)

  def moving(enemy: Enemy): Behavior[ModelMessage] =
    Behaviors.withTimers(timer => {
      Behaviors.receive((ctx, msg) => {
        msg match
          case Update(timeElapsed, entities, replyTo) =>
            val updatedEnemy = enemy.update(timeElapsed, entities)
            replyTo ! EntityUpdated(ctx.self, updatedEnemy)
            moving(updatedEnemy)

          case Shoot(replyTo) => ???

          case Collision(entity, replyTo) => ???

          case _ => Behaviors.same
      })
    })
