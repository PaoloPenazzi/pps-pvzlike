import DefaultTurretValues.*

object Turrets:
  trait Turret extends Entity with AttackingEntity :
    def bullet: Bullet
    def cost: Int = costs(this)

  /**
   * Basic turret.
   *
   * @param position The position in which the plant is placed by the player.
   */
  class Plant(override val position: (Int, Int)) extends Turret :
    override def boundary: (Int, Int) = (10, 10)
    override def bullet: Seed = new Seed(position)

  class Zombie(override val position: (Int, Int)) extends MovingEntity, AttackingEntity :
    override def boundary: (Int, Int) = (10, 10)
    override def velocity: Double = 1.0

object DefaultTurretValues:
  import Turrets.*
  val healthPoints: AttackingEntity => Int =
    case _: Plant => 100
    case _: Zombie => 100
    case _ => 0

  val fireRates: AttackingEntity => Double =
    case _: Plant => 1.0
    case _: Zombie => 0.8
    case _ => 0

  val costs: Turret => Int =
    case _: Plant => 100
    case _        => 0

  val damages: Bullet => Int =
    case _: Seed => 25
    case _       => 0

  val ranges: AttackingEntity => Double =
    case _: Plant => 5.0
    case _: Zombie => 1.0
    case _ => 0
