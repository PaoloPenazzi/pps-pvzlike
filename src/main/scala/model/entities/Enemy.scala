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
    case turret: Turret => turret.position.y == position.y && turret.position.x < position.x && position.x - turret.position.x.toInt <= range
    case _ => false

/**
 * Basic Enemy.
 */
case class Zombie(override val position: Position,
             override val life: Int = 100,
             override val state: TroopState = Moving,
             override val velocity: Float = -0.01) extends Enemy:

  override def canAttack(entity: Entity): Boolean =
    entity.position.y == position.y && entity.position.x < position.x && position.x - entity.position.x.toInt <= range

  override type BulletType = Paw
  override def bullet: BulletType = new Paw(position)

  override def collideWith(bullet: Bullet): Troop =
    val newLife = Math.max(life - bullet.damage, 0)
    Zombie(position, newLife, if newLife == 0 then Dead else state)
  
  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Enemy =
    val nextState = if interests.nonEmpty then Attacking else Moving
    state match
      case Moving => copy(position = updatePosition(elapsedTime), state = nextState)
      case Attacking => copy(state = nextState)
      case _ => this



