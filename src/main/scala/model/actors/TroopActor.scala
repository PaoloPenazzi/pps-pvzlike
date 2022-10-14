package model.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.GameLoopCommands.{EntityDead, EntitySpawned, EntityUpdated}
import model.entities.*
import model.actors.BulletActor

import concurrent.duration.DurationInt

object TroopActor:
  def apply(troop: Troop): Behavior[ModelMessage] =
    standardBehaviour(troop)

  def standardBehaviour(troop: Troop): Behavior[ModelMessage] =
    Behaviors.withTimers(timer => {
      Behaviors.receive((ctx, msg) => {
        msg match
          case Update(elapsedTime, entities, replyTo) =>
            val entityUpdated: Entity = troop.update(elapsedTime, entities)
            replyTo ! EntityUpdated(ctx.self, entityUpdated)
                entities.find(enemy => troop canAttack enemy) match
                  case Some(_) =>
                    if !timer.isTimerActive("Shooting")
                    then timer.startSingleTimer("Shooting", Shoot(replyTo), troop.fireRate.seconds)
                  case _ =>
            standardBehaviour(entityUpdated.asInstanceOf[Troop])

          case Shoot(replyTo) =>
            val bullet: Bullet = troop.asInstanceOf[AttackingAbility].bullet
            val bulletActor = ctx.spawnAnonymous(BulletActor(bullet))
            replyTo ! EntitySpawned(bulletActor, bullet)
            Behaviors.same

          case Collision(entity, replyTo) =>
            val entityUpdated: Option[Troop] = troop collideWith entity.asInstanceOf[Bullet]
            entityUpdated match
              case None =>
                replyTo ! EntityDead(ctx.self)
                Behaviors.stopped
              case _ =>
                replyTo ! EntityUpdated(ctx.self, entityUpdated.get)
                standardBehaviour(entityUpdated.get)

          case _ => Behaviors.same
      })
    })

