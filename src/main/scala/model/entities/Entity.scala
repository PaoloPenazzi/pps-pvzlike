package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}

trait Entity:
  def width: Int = DefaultValues.width(this)

trait StationaryEntity extends Entity:
  def position: Position

trait MovingEntity() extends Entity:
  var position: Position = (0,0f)
  def velocity: Float

trait AttackingEntity extends Entity :
  var healthPoints: Int = HP(this)
  def fireRate: Int = fireRates(this)
  def range: Int = ranges(this)