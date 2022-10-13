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
              case _: AttackingAbility =>
                entities.find(enemy => troop.asInstanceOf[AttackingAbility] canAttack enemy) match
                  case Some(_) =>
                    if !timer.isTimerActive("Shooting")
                    then
                      timer.startSingleTimer("Shooting", Shoot(replyTo), troop.asInstanceOf[AttackingAbility].fireRate.seconds)
                  case _ =>
              case _ =>
            standardBehaviour(entityUpdated.asInstanceOf[Troop])

          case Shoot(replyTo) =>
            val bullet: Bullet = troop.asInstanceOf[AttackingAbility].bullet
            val bulletActor = ctx.spawnAnonymous(BulletActor(bullet))
            replyTo ! EntitySpawned(bulletActor, bullet)
            Behaviors.same

          case Collision(bullet, replyTo) =>
            val entityUpdated = troop.updateAfterCollision(bullet)
            replyTo ! EntityUpdated(ctx.self, entityUpdated)
            standardBehaviour(entityUpdated)

          case _ => Behaviors.same
      })
    })

