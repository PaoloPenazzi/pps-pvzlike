import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors
import Turrets.*

trait TurretMessages
case class Update(timeElapsed: Double, entities: List[Entity]) extends TurretMessages

object TurretActor:
  def apply(turret: Turret): Behavior[TurretMessages] =
    detecting(turret)

  def detecting(turret: Turret): Behavior[TurretMessages] =
    Behaviors.receiveMessage(msg => {
      msg match
        case Update(timeElapsed, entities) =>
          entities.collect{case e: Enemy => e}
            .sortWith((e1, e2) => e1.position._1 < e2.position._1)
            .find(e=> e.position._2 == turret.position._2) match
            case Some(e) => ???
            case _ => ???
          Behaviors.same

        case _ =>
          ???
          Behaviors.same
    })