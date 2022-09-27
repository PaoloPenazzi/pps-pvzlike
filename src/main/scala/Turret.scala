import DefaultValues.*

object Turrets:
  trait Turret extends Entity with AttackingEntity with StationaryEntity:
    def cost: Int = costs(this)
    
    def canAttack(enemy: Enemy): Boolean =
      isOnMyPath(enemy) && isInRange(enemy)

    private def isOnMyPath(enemy: Enemy): Boolean =
      enemy.position._2 == position._2
      
    private def isInRange(enemy: Enemy): Boolean =
      enemy.position._1 <= range
    
    
  /**
   * Basic turret.
   *
   * @param position The position in which the plant is placed by the player.
   */
  class Plant(override val position: (Int, Int)) extends Turret

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

  val ranges: AttackingEntity => Int =
    case _: Plant => 500
    case _: Zombie => 10
    case _ => 0
