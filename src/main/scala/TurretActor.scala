import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors

trait TurretMessages
case class Update() extends TurretMessages

object TurretActor:
  def apply(): Behavior[TurretMessages] =
    Behaviors.receive { (context, message) =>
      message match
        case _ =>
          Behaviors.same
    }
