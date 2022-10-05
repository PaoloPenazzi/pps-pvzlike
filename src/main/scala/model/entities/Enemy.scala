package model.entities

import model.entities.WorldSpace.Position

trait Enemy extends MovingEntity with AttackingEntity:
  override type UpdatedEntity = Enemy
  override def velocity: Float = 1.0
class Zombie(override val position: Position) extends Enemy:
  override def update(elapsedTime: Float, interests: List[Entity]): Enemy =
    Zombie(updatePosition(elapsedTime))
