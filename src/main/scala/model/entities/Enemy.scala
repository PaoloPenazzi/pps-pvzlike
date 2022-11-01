package model.entities

import model.common.DefaultValues
import model.entities.TroopState.*
import model.entities.WorldSpace.Position

import scala.concurrent.duration.FiniteDuration

/**
 * Interface of an enemy model
 */
abstract class Enemy(override val position: Position,
            override val life: Int,
            override val state: TroopState,
            override val velocity: Float) extends Troop with MovingAbility:

  override def isInterestedIn: Entity => Boolean =
    case plant: Plant => plant.position.y == position.y && plant.position.x < position.x && position.x - plant.position.x.toInt <= range
    case _ => false

  override def canAttack(entity: Entity): Boolean =
    entity.position.y == position.y && entity.position.x < position.x && position.x - entity.position.x.toInt <= range

  override def collideWith(bullet: Bullet): Troop =
    val newLife = Math.max(life - bullet.damage, 0)
    //copy(position, newLife, if newLife == 0 then Dead else state, velocity)
    //val enemy = (this withPosition position).withLife(newLife)
    if newLife == 0
    then this withLife newLife withState Dead
    else this withLife newLife withState state

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Troop =
    val nextState = if interests.nonEmpty then Attacking else Moving
    state match
      case Moving => (this withPosition updatePosition(elapsedTime)).withState(nextState)
      case Attacking => this withState nextState
      case _ => this

  private def updatePosition(elapsedTime: FiniteDuration): Position =
    (position.y, position.x + (elapsedTime.length * velocity))


/**
 * Basic Zombie Enemy.
 */
case class Zombie(override val position: Position = (0,0),
             override val life: Int = 150,
             override val state: TroopState = Moving,
             override val velocity: Float = -0.01) extends Enemy(position, life, state, velocity):
  override def bullet: Bullet = PawBullet(position)
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)
  override def withState(newState: TroopState): Troop = copy(state = newState)

