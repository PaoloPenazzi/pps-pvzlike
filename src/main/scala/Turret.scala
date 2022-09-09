trait Entity:
  def boundary: (Int, Int)
  def position: (Int, Int)

trait AttackingEntity extends Entity:
  def healthPoints: Int
  def fireRate: Double

trait MovingEntity extends Entity:
  def velocity: Double

trait Turret extends Entity with AttackingEntity:
  def bullet: Bullet
  def cost: Int

trait Bullet extends MovingEntity:
  def damage: Int

/**
 * Basic bullet shot by a plant.
 * @param position The initial position of the bullet.
 */
class Seed(override val position: (Int, Int)) extends Bullet:
  override def damage: Int = 25
  override def velocity: Double = 5.0
  override def boundary: (Int, Int) = (2, 2)

/**
 * Basic turret.
 * @param position The position in which the plant is placed by the player.
 */
class Plant(override val position: (Int, Int)) extends Turret:
  override def cost: Int = 100
  override def fireRate: Double = 1.0
  override def healthPoints: Int = 100
  override def boundary: (Int, Int) = (10, 10)
  override def bullet: Seed = new Seed(position)



