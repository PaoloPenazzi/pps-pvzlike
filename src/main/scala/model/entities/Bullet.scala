package model.entities

import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}

trait Bullet extends MovingEntity with Entity:
  override type UpdatedEntity = Bullet
  def damage: Int = damages(this)
  def filter: Entity => Boolean =
    _ => false
  def shouldDisappearAfterHitting(entity: Entity): Boolean = true

class Seed(override val position: Position) extends Bullet:
  override def velocity: Float = 5.0

  override def update(elapsedTime: Float, interests: List[Entity]): Bullet =
    Seed(updatePosition(elapsedTime))
