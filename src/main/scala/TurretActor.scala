import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors
import scala.concurrent.duration.DurationInt
import Turrets.*

trait TurretMessages
case class Update(entities: List[Entity]) extends TurretMessages
case class Shoot(enemy: Enemy) extends TurretMessages
case class Hit(damage: Int) extends TurretMessages

object TurretActor:
  def apply(turret: Turret): Behavior[TurretMessages] =
    detecting(turret)

  def detecting(turret: Turret): Behavior[TurretMessages] =
    Behaviors.withTimers(timer => {
      Behaviors.receiveMessage(msg => {
        msg match
          case Update(entities) =>
            entities.collect { case enemy: Enemy => enemy }
              .sortWith((e1, e2) => e1.position._1 <= e2.position._1)
              .find(enemy => turret isInRange enemy) match
              case Some(enemy) =>
                timer.startSingleTimer(Shoot(enemy), turret.fireRate.seconds)
                Behaviors.same
              case _ => Behaviors.same

          case Shoot(enemy) =>
            // Notify the controller that you shoot the enemy
            Behaviors.same

          case Hit(damage) =>
            turret.healthPoints = turret.healthPoints - damage
            turret.healthPoints match
              case 0 =>
                // Notify controller that the turret is detroyed
                Behaviors.stopped
              case _ => Behaviors.same

          case _ => Behaviors.same
      })
    })