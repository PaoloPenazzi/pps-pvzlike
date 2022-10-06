package model.entities

import model.entities.WorldSpace.Position

import scala.concurrent.duration.FiniteDuration

/**
 * Interface of an enemy model
 */
trait Enemy extends MovingEntity with AttackingEntity:
  override type UpdatedEntity = Enemy

  def canAttack(turret: Turret): Boolean =
    isOnMyPath(turret) && isInRange(turret)

  protected def isOnMyPath(turret: Turret): Boolean

  protected def isInRange(turret: Turret): Boolean

  /**
   * filters to keep only all game entities present on the same lane
   * @return true if it is of his own interest
   */
  override def filter: Entity => Boolean

/**
 * Basic Enemy.
 */
class Zombie(override val position: Position) extends Enemy:
  override def velocity: Float = -0.001
  override def isOnMyPath(turret: Turret): Boolean =
    turret.position.y == position.y
  override def isInRange(turret: Turret): Boolean =
    position.x - turret.position.x.toInt <= range
  override def filter: Entity => Boolean =
    case turret: Turret => turret.position.y == position.y
    case _ => false
  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Enemy =
    Zombie(updatePosition(elapsedTime))

