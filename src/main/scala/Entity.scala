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
  def position: (Int, Int)

trait AttackingEntity extends Entity :
  def hp: Int = healthPoints(this)
  def fireRate: Double = fireRates(this)
  def range: Double = ranges(this)

trait MovingEntity extends Entity :
  def velocity: Double