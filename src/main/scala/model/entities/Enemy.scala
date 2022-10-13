package model.entities

import model.common.DefaultValues
import model.entities.WorldSpace.Position

import scala.concurrent.duration.FiniteDuration

/**
 * Interface of an enemy model
 */
trait Enemy extends MovingAbility with AttackingAbility with Troop:

  override def isInterestedIn: Entity => Boolean =
    case turret: Turret => turret.position.y == position.y
    case _ => false

/**
 * Basic Enemy.
 */
class Zombie(override val position: Position, override val life: Int = 100) extends Enemy:

  override def collideWith(bullet: Bullet): Option[Enemy] = ???
   
  override def velocity: Float = -0.001
  
  override def bullet: Bullet = new Seed(position)

  override def canAttack(turret: Entity): Boolean =
    isInterestedIn(turret) && position.x - turret.position.x.toInt <= range

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Enemy =
    Zombie(updatePosition(elapsedTime))


