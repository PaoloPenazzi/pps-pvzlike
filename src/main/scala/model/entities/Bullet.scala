package model.entities

import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}

trait Bullet extends MovingEntity with Entity:
  def damage: Int = damages(this)

  def updatePositionAfter(timeElapsed: Float): Unit =
    position = (position.y, position.x + (timeElapsed * velocity))

  def filter: Entity => Boolean =
    _ => false

  def shouldDisappearAfterHitting(entity: Entity): Boolean = true

class Seed() extends Bullet:
  override def velocity: Float = 5.0