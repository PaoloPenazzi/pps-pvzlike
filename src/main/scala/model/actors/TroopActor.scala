package model.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.GameLoopCommands.{EntitySpawned, EntityUpdated}
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
            troop match
              case _: AttackingEntity =>
                entities.find(enemy => troop.asInstanceOf[AttackingEntity] canAttack enemy) match
                  case Some(_) =>
                    if !timer.isTimerActive("Shooting")
                    then
                      timer.startSingleTimer("Shooting", Shoot(replyTo), troop.asInstanceOf[AttackingEntity].fireRate.seconds)
                  case _ =>
              case _ =>
            standardBehaviour(entityUpdated.asInstanceOf[Troop])

          case Shoot(replyTo) =>
            val bullet: Bullet = troop.asInstanceOf[AttackingEntity].getBullet
            val bulletActor = ctx.spawnAnonymous(BulletActor(bullet))
            replyTo ! EntitySpawned(bulletActor, bullet)
            Behaviors.same

          case Collision(entity, replyTo) => ???

          case _ => Behaviors.same
      })
    })

