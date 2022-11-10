package model.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.actors.GameLoopActor.GameLoopCommands.{BulletSpawned, EndReached, EntityDead, EntityUpdated}
import model.actors.BulletActor
import model.entities.*
import model.entities.TroopState.*
import model.entities.WorldSpace.*
import view.Game

import scala.concurrent.duration.DurationInt

object TroopActor:
  private val shootingTimerKey: String = "Shooting"
  private val standardElapsedTime = 16

  def apply(troop: Troop): Behavior[ModelMessage] =
    standardBehaviour(troop)

  def standardBehaviour(troop: Troop): Behavior[ModelMessage] =
    Behaviors.withTimers(timer => {
      Behaviors.receive((ctx, msg) => {
        msg match
          case Update(elapsedTime, entities, replyTo) =>
            val entityUpdated: Troop = troop.update(elapsedTime, entities)
            if entityUpdated.position.x < endGameLimit then
              replyTo ! EndReached()
              Behaviors.same
            replyTo ! EntityUpdated(ctx.self, entityUpdated)
            entityUpdated.state match
              case Attacking =>
                if !timer.isTimerActive(shootingTimerKey)
                then timer.startSingleTimer(shootingTimerKey, Shoot(replyTo),
                  troop.fireRate.seconds * standardElapsedTime / elapsedTime.toMillis)
              case _ => timer cancel shootingTimerKey
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
                    replyTo ! EntityDead(ctx.self, Some(troop))
                    ctx.children collect { case a: ActorRef[ModelMessage] => a } foreach (ref => replyTo ! EntityDead(ref, None))
                    Behaviors.stopped
                  case _ =>
                    replyTo ! EntityUpdated(ctx.self, entityUpdated)
                    standardBehaviour(entityUpdated)
              case _ => Behaviors.same
            }
          case _ => Behaviors.same
      })
    })