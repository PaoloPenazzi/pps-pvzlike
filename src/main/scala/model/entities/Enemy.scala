package model.entities

import model.common.DefaultValues
import model.entities.WorldSpace.Position

import scala.concurrent.duration.FiniteDuration

/**
 * Interface of an enemy model
 */
trait Enemy extends MovingAbility with AttackingAbility with Troop:

  override def isInterestedIn: Entity => Boolean =
    case turret: Turret => (turret.position.y == position.y) &&
                           (position.x - turret.position.x.toInt <= range)
    case _ => false

/**
 * Basic Enemy.
 */
class Zombie(override val position: Position,
             override val life: Int = 100,
             override val velocity: Float = -0.001) extends Enemy:

  import model.entities.Turret
  import model.entities.Seed

  override def canAttack(turret: Entity): Boolean =
    isInterestedIn(turret)

  override def collideWith(bullet: Bullet): Option[Enemy] =
    val newLife = life - bullet.damage
    if newLife <= 0 then None else Some(Zombie(position, newLife, velocity))
  
  override def bullet: Bullet = new Seed(position)
  
  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Enemy =
    Zombie(updatePosition(elapsedTime))


