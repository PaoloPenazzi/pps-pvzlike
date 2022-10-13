package model.entities

import model.common.DefaultValues
import model.entities.WorldSpace.Position

import scala.concurrent.duration.FiniteDuration

/**
 * Interface of an enemy model
 */
trait Enemy extends MovingEntity with AttackingEntity with Troop:
  override type UpdatedEntity = Enemy


/**
 * Basic Enemy.
 */
class Zombie(override val position: Position,
             override val life: Int = 100,
             override val velocity: Float = -0.001) extends Enemy:

  import model.entities.Turret
  import model.entities.Seed

  override def updateAfterCollision(entity: Entity): Enemy =
    entity match
      case seed: Seed => Zombie(position, life - seed.damage, velocity)
      case _ => Zombie(position, life, velocity)

  override def interest: Entity => Boolean =
    case turret: Turret => (turret.position.y == position.y) && position.x - turret.position.x.toInt <= range)
    case _ => false

  override def getBullet: Bullet = new Seed(position)

  override def canAttack(turret: Entity): Boolean =
    interest(turret)

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Enemy =
    Zombie(updatePosition(elapsedTime))


