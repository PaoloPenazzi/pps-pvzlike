trait Entity:
  def boundary: (Int, Int)
  def position: (Int, Int)
  def healthPoints: Int

trait AttackingEntity extends Entity:
  def fireRate: Int

trait MovingEntity extends Entity:
  def velocity: Double

trait Turret extends Entity with AttackingEntity with MovingEntity:
  def cost: Int





