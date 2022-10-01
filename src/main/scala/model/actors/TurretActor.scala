package model.actors

import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors
import controller.Command
import controller.GameLoopActor.GameLoopCommands.{EntitySpawned}
import model.actors
import model.entities.{Enemy, Entity, Seed}
import scala.concurrent.duration.DurationInt
import model.entities.{Bullet, Turret}


object TurretActor:
  def apply(turret: Turret): Behavior[ModelMessage] =
    detecting(turret)

  def detecting(turret: Turret): Behavior[ModelMessage] =
    Behaviors.withTimers(timer => {
      Behaviors.receive((ctx, msg) => {
        msg match
          case Update(_, entities, replyTo) =>
            entities.collect { case enemy: Enemy => enemy }
              .find(enemy => turret canAttack enemy) match
              case Some(_) =>
                if !timer.isTimerActive("TurretShooting")
                then timer.startSingleTimer("TurretShooting", Shoot(replyTo), turret.fireRate.seconds)
                Behaviors.same
              case _ => Behaviors.same

          case Shoot(replyTo) =>
            val bullet = new Seed
            val bulletActor = ctx.spawnAnonymous(BulletActor(bullet))
            replyTo ! EntitySpawned(bulletActor, bullet)
            Behaviors.same

          case Collision(entity, replyTo) => ???

          case _ => Behaviors.same
      })
    })