package model.entities

trait Enemy extends Entity with MovingEntity with AttackingEntity:
  override def velocity: Double = 1.0

  def canAttack(turret: Turret): Boolean =
    isOnMyPath(turret) && isInRange(turret)

  private def isOnMyPath(turret: Turret): Boolean =
    turret.position._2 == position._2

  private def isInRange(turret: Turret): Boolean =
    turret.position._1.toInt <= range

/**
 * Basic zombie.
 */
case class Zombie() extends Enemy