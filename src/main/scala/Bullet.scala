import DefaultValues.*

trait Bullet extends MovingEntity:
  def damage: Int = damages(this)

/**
 * Basic bullet shot by a plant.
 *
 * @param position The initial position of the bullet.
 */
class Seed() extends Bullet :
  override def velocity: Double = 5.0
  override def direction: String = "right"