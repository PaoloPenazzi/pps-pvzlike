import DefaultValues.*

trait Entity:
  def boundary: (Int, Int)
  def position: (Int, Int)

trait AttackingEntity extends Entity :
  def hp: Int = healthPoints(this)
  def fireRate: Double = fireRates(this)
  def range: Double = ranges(this)

trait MovingEntity extends Entity :
  def velocity: Double