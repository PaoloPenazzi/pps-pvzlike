package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*

trait Entity:
  def width: Int = DefaultValues.width(this)

trait StationaryEntity extends Entity:
  def position: (Int, Int)

abstract class MovingEntity() extends Entity:
  var position: (Int, Int) = _
  def velocity: Double

trait AttackingEntity extends Entity :
  var healthPoints: Int = HP(this)
  def fireRate: Int = fireRates(this)
  def range: Int = ranges(this)