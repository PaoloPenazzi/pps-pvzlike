import DefaultValues.*

/**
 * The Boundary class represent the entity's shape.
 * Each entity is a rectangle with a specified dimension.
 * The bottom left vertex of the rectangle correspond to the entity position.
 *
 * @param x The length of the entity shape.
 * @param y The height of the entity shape.
 */
case class Boundary(x: Double, y: Double)
    

trait Entity:
  def boundary: Boundary = boundaries(this)

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