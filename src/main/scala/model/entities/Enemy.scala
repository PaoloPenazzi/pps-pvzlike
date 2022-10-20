package model.entities

import model.common.DefaultValues
import model.entities.TroopState.*
import model.entities.WorldSpace.Position

import scala.concurrent.duration.FiniteDuration

/**
 * Interface of an enemy model
 */
trait Enemy extends Troop with MovingAbility:

  override def isInterestedIn: Entity => Boolean =
    case turret: Turret => turret.position.y == position.y && position.x - turret.position.x.toInt <= range
    case _ => false

/**
 * Basic Enemy.
 */
class Zombie(override val position: Position,
             override val life: Int = 100,
             override val state: TroopState = Moving,
             override val velocity: Float = -0.001) extends Enemy:
  
  override def bullet: Bullet = new PeaBullet(position)

  override def collideWith(bullet: Bullet): Troop =
    val newLife = Math.max(life - bullet.damage, 0)
    Zombie(position, newLife, if newLife == 0 then Dead else state)
  
  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Enemy =
    val shouldBeAttacking = interests.nonEmpty
    state match
      case Moving => Zombie(
        updatePosition(elapsedTime),
        life,
        if shouldBeAttacking then Attacking else state)
      case Attacking => Zombie(
        position,
        life,
        if shouldBeAttacking then state else Moving)
      case _ => this



