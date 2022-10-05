package model.entities

import model.entities.WorldSpace.Position

import scala.concurrent.duration.FiniteDuration

trait Enemy extends MovingEntity with AttackingEntity:
  override type UpdatedEntity = Enemy
  override def velocity: Float = -0.001
/**
 * Basic zombie.
 */
class Zombie(override val position: Position) extends Enemy:
  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Enemy =
    Zombie(updatePosition(elapsedTime))

