package model.entities

import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}
import scala.concurrent.duration.FiniteDuration

trait Bullet extends MovingEntity with Entity:
  override type UpdatedEntity = Bullet
  def damage: Int = damages(this)
  def shouldDisappearAfterHitting(entity: Entity): Boolean = true

class Seed(override val position: Position) extends Bullet:
  override def velocity: Float = 0.1

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Bullet =
    Seed(updatePosition(elapsedTime))
