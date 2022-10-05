package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}

trait Entity:
  type UpdatedEntity <: Entity
  def width: Int = DefaultValues.width(this)
  def position: Position
  def update(elapsedTime: Float, interest: List[Entity]): UpdatedEntity

trait MovingEntity() extends Entity:
  def velocity: Float
  def updatePosition(elapsedTime: Float): Position =
    (position.y, position.x + (elapsedTime * velocity))

trait AttackingEntity extends Entity :
  def hp: Int = 1
  def fireRate: Int = fireRates(this)
  def range: Int = ranges(this)