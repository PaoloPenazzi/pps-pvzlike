import DefaultValues.*

/**
 * The boundary of the entity, thus the area where the entity is rendered by the view.
 *
 * @param x
 * @param y
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