import DefaultValues.*

object Turrets:
  trait Turret extends Entity with AttackingEntity:
    def bullet: Bullet
    def cost: Int = costs(this)
    def isInRange(enemy: Enemy): Boolean =
      ???

  /**
   * Basic turret.
   *
   * @param position The position in which the plant is placed by the player.
   */
  class Plant(override val position: (Int, Int)) extends Turret:
    override def bullet: Seed = new Seed(position)

  

object DefaultValues:
  import Turrets.*

  val boundaries: Entity => Boundary =
    case _: Bullet => Boundary(5, 5)
    case _ => Boundary(20, 40)

  val healthPoints: AttackingEntity => Int =
    case _: Plant => 300
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
