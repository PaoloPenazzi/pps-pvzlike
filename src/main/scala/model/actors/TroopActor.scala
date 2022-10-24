package model.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.GameLoopCommands.{EntityDead, BulletSpawned, EntityUpdated}
import model.entities.*
import model.actors.BulletActor
import model.entities.TroopState.*

import concurrent.duration.DurationInt

object TroopActor:
  private val ShootingTimerKey: String = "Shooting"
  def apply(troop: Troop): Behavior[ModelMessage] =
    standardBehaviour(troop)

  def standardBehaviour(troop: Troop): Behavior[ModelMessage] =
    Behaviors.withTimers(timer => {
      Behaviors.receive((ctx, msg) => {
        msg match
          case Update(elapsedTime, entities, replyTo) =>
            val entityUpdated: Troop = troop.update(elapsedTime, entities)
            replyTo ! EntityUpdated(ctx.self, entityUpdated)
            entityUpdated.state match
              case Attacking =>
                if !timer.isTimerActive(ShootingTimerKey)
                then timer.startSingleTimer(ShootingTimerKey, Shoot(replyTo), troop.fireRate.seconds)
              case _ => timer cancel ShootingTimerKey
            standardBehaviour(entityUpdated)

          case Shoot(replyTo) =>
            val bullet: Bullet = troop.bullet
            val bulletActor = ctx.spawnAnonymous(BulletActor(bullet))
            replyTo ! BulletSpawned(bulletActor, bullet)
            Behaviors.same

          case Collision(entity, replyTo) =>
            entity match {
              case bullet: Bullet =>
                val entityUpdated: Troop = troop collideWith bullet
                entityUpdated.state match
                  case Dead =>
                    replyTo ! EntityDead(ctx.self)
                    Behaviors.stopped
                  case _ =>
                    replyTo ! EntityUpdated(ctx.self, entityUpdated)
                    standardBehaviour(entityUpdated)
              case _ => Behaviors.same
            }

          case _ => Behaviors.same
      })
    })

