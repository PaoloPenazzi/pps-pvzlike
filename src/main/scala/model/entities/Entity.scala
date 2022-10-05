package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}

import scala.concurrent.duration.FiniteDuration

trait Entity:
  type UpdatedEntity <: Entity
  def width: Int = DefaultValues.width(this)
  def position: Position
  def update(elapsedTime: FiniteDuration, interest: List[Entity]): UpdatedEntity
  def filter: Entity => Boolean = _ => false

trait MovingEntity() extends Entity:
  def velocity: Float
  def updatePosition(elapsedTime: FiniteDuration): Position =
    (position.y, position.x + (elapsedTime.length * velocity))

trait AttackingEntity extends Entity :
  def hp: Int = 1
  def fireRate: Int = fireRates(this)
  def range: Int = ranges(this)