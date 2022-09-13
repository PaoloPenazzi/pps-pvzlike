import DefaultTurretValues.*

trait Bullet extends MovingEntity :
  def damage: Int = damages(this)

/**
 * Basic bullet shot by a plant.
 *
 * @param position The initial position of the bullet.
 */
class Seed(override val position: (Int, Int)) extends Bullet :
  override def velocity: Double = 5.0

  override def boundary: (Int, Int) = (2, 2)