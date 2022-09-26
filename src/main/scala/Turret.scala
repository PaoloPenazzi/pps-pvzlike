import DefaultValues.*

object Turrets:
  trait Turret extends Entity with AttackingEntity:
    def bullet: Bullet
    def cost: Int = costs(this)
    def isInRange(enemy: Enemy): Boolean =
      enemy.position._2 == position._2

  /**
   * Basic turret.
   *
   * @param position The position in which the plant is placed by the player.
   */
  class Plant(override val position: (Int, Int)) extends Turret:
    override def bullet: Seed = new Seed()

  

object DefaultValues:
  import Turrets.*

  val boundaries: Entity => Boundary =
    case _: Bullet => Boundary(5, 5)
    case _ => Boundary(20, 40)

  val HP: AttackingEntity => Int =
    case _: Plant => 300
    case _: Zombie => 100
    case _ => 0

  val fireRates: AttackingEntity => Int =
    case _: Plant => 2
    case _: Zombie => 3
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
