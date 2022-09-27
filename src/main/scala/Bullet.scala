import DefaultValues.*

trait Bullet extends MovingEntity with Entity:
  def damage: Int = damages(this)

class Seed() extends Bullet:
  override def velocity: Double = 5.0
  override def direction: String = "right"