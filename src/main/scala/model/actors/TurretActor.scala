package model.actors

import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors
import model.actors
import model.entities.{Enemy, Entity, Seed}

import scala.concurrent.duration.DurationInt
import model.entities.{Bullet, Turret}

trait CommonMessages

case class Update(timeElapsed: Double, entities: List[Entity], replyTo: ActorRef[CommonMessages]) extends CommonMessages

trait TurretMessages extends CommonMessages

case class Shoot() extends TurretMessages

case class Hit(damage: Int) extends TurretMessages

object TurretActor:
  def apply(turret: Turret): Behavior[TurretMessages] =
    detecting(turret)

  def detecting(turret: Turret): Behavior[TurretMessages] =
    Behaviors.withTimers(timer => {
      Behaviors.receive((ctx, msg) => {
        msg match
          case Update(_, entities, _) =>
            entities.collect { case enemy: Enemy => enemy }
              .find(enemy => turret canAttack enemy) match
              case Some(_) =>
                if !timer.isTimerActive("TurretShooting")
                then timer.startSingleTimer("TurretShooting", actors.Shoot(), turret.fireRate.seconds)
                Behaviors.same
              case _ => Behaviors.same

          case Shoot() =>
            val bullet = new Seed
            val bulletActor = ctx.spawnAnonymous(BulletActor(bullet))
            // Send entity creation message
            Behaviors.same

          case Hit(damage) =>
            turret.healthPoints = turret.healthPoints - damage
            turret.healthPoints match
              case 0 => Behaviors.stopped
              case _ => Behaviors.same

          case _ => Behaviors.same
      })
    })