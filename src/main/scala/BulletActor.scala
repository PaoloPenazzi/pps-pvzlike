import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

trait BulletMessages

object BulletActor:
  def apply(bullet: Bullet): Behavior[BulletMessages] =
    moving(bullet)

  def moving(bullet: Bullet): Behavior[BulletMessages] =
    Behaviors.receiveMessage(msg => {
      msg match
        case _ => Behaviors.same
    })
  