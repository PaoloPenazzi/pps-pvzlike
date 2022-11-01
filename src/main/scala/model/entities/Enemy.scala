package model.entities

import model.common.DefaultValues
import model.entities.TroopState.*
import model.entities.WorldSpace.{LanesLength, NumOfLanes, Position}

import scala.concurrent.duration.FiniteDuration
import scala.util.Random

/**
 * A Enemy is an abstract entity that models the common behaviour of different types of enemy.
 *
 * @param position the position in which the enemy is placed.
 * @param life the life that the enemy currently has.
 * @param state the state of the enemy.
 */
abstract class Enemy(override val position: Position,
            override val life: Int,
            override val state: TroopState) extends Troop with MovingAbility:

  override def isInterestedIn: Entity => Boolean =
    case plant: Plant => plant.position.y == position.y && plant.position.x < position.x && position.x - plant.position.x.toInt <= range
    case _ => false
  
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
 * Zombie is the base enemy of the game.
 *
 * @param position the position in which the zombie is placed.
 * @param life the life that the zombie currently has.
 * @param state the state of the zombie.
 */
case class Zombie(override val position: Position = (Random.between(0, NumOfLanes), LanesLength + Random.between(0, 20)),
             override val life: Int = 100,
             override val state: TroopState = Moving) extends Enemy(position, life, state):
  override val velocity: Float = -0.01
  override def bullet: Bullet = PawBullet(position)
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)
  override def withState(newState: TroopState): Troop = copy(state = newState)

/**
 * A FastZombie is a zombie that moves and attacks faster but has less life than a base zombie.
 *
 * @param position the position in which the zombie is placed.
 * @param life the life that the zombie currently has.
 * @param state the state of the zombie.
 */
case class FastZombie(override val position: Position = (Random.between(0, NumOfLanes), LanesLength + Random.between(0, 20)),
                      override val life: Int = 80,
                      override val state: TroopState = Moving) extends Enemy(position, life, state):
  override val velocity: Float = -0.02
  override def bullet: Bullet = PawBullet(position)
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)
  override def withState(newState: TroopState): Troop = copy(state = newState)


/**
 * A WarriorZombie is a zombie that moves more slowly but does a lot of damage.
 *
 * @param position the position in which the zombie is placed.
 * @param life the life that the zombie currently has.
 * @param state the state of the zombie.
 */
case class WarriorZombie(override val position: Position = (Random.between(0, NumOfLanes), LanesLength + Random.between(0, 20)),
                         override val life: Int = 200,
                         override val state: TroopState = Moving) extends Enemy(position, life, state):
  override val velocity: Float = -0.005
  override def bullet: Bullet = SwordAttack(position)
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)
  override def withState(newState: TroopState): Troop = copy(state = newState)


