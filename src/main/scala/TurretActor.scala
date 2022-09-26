import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors
import scala.concurrent.duration.DurationInt
import Turrets.*

trait TurretMessages
case class Update(entities: List[Entity]) extends TurretMessages
case class Shoot(enemy: Enemy) extends TurretMessages

object TurretActor:
  def apply(turret: Turret): Behavior[TurretMessages] =
    detecting(turret)

  def detecting(turret: Turret): Behavior[TurretMessages] =
    Behaviors.withTimers(timer => {
      Behaviors.receiveMessage(msg => {
        msg match
          case Update(entities) =>
            entities.collect { case e: Enemy => e }
              .sortWith((e1, e2) => e1.position._1 <= e2.position._1)
              .find(e => turret isInRange e) match
              case Some(e) =>
                timer.startSingleTimer(Shoot(e), turret.fireRate.seconds)
                Behaviors.same
              case _ => Behaviors.same

          case Shoot(enemy) =>
            // Notify the controller that you shoot the enemy
            Behaviors.same

          case _ => Behaviors.same
      })
    })