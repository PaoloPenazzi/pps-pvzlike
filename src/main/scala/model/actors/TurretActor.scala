package model.actors

import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors
import model.actors
import model.entities.{Enemy, Entity, Seed}

import scala.concurrent.duration.DurationInt
import model.entities.Turrets.*
import model.entities.Bullet

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
              .sortWith((e1, e2) => e1.position._1 <= e2.position._1)
              .find(enemy => turret canAttack enemy) match
              case Some(_) =>
                timer.startSingleTimer(actors.Shoot(), turret.fireRate.seconds)
                Behaviors.same
              case _ => Behaviors.same

          case Shoot() =>
            ctx.spawnAnonymous(BulletActor(new Seed))
            Behaviors.same

          case Hit(damage) =>
            turret.healthPoints = turret.healthPoints - damage
            turret.healthPoints match
              case 0 => Behaviors.stopped
              case _ => Behaviors.same

          case _ => Behaviors.same
      })
    })