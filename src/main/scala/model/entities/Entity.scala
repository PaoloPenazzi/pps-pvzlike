package model.entities

import model.common.DefaultValues.*

object ShapeBuilder:
  def apply(position: (Int, Int), width: Int, height: Int): Shape =
    Shape(
      (position._1, position._2),
      (position._1 + width, position._2),
      (position._1, position._2 + height),
      (position._1 + width, position._2 + height)
    )

  case class Shape(val x0: (Int, Int), val x1: (Int, Int), val y1:(Int, Int), val y2: (Int, Int))

trait Entity:
  def w: Int = width(this)
  def h: Int = height(this)

trait StationaryEntity extends Entity:
  def position: (Int, Int)

abstract class MovingEntity() extends Entity:
  var position: (Int, Int) = _
  def velocity: Double
  def direction: String

trait AttackingEntity extends Entity :
  var healthPoints: Int = HP(this)
  def fireRate: Int = fireRates(this)
  def range: Int = ranges(this)