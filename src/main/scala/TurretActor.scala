import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors
import Turrets.*

trait TurretMessages
case class CheckIfEnemyIsInRange() extends TurretMessages

object TurretActor:
  def apply(turret: Turret): Behavior[TurretMessages] =
    detecting(turret)

  def detecting(turret: Turret): Behavior[TurretMessages] =
    Behaviors.receiveMessage(msg => {
      msg match
        case CheckIfEnemyIsInRange() =>
          ???
          Behaviors.same

        case _ =>
          ???
          Behaviors.same
    })