package model.entities

import model.common.DefaultValues.*
import model.entities.TroopState.*
import model.entities.WorldSpace.{Position, given}
import model.entities.{AttackingAbility, Bullet, Zombie, Entity, PeaBullet, Plant, BasicZombie}

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

/**
 * A plant is an abstract entity that models the common behaviour of different types of plants.
 *
 * @param position the position in which the plant is placed.
 * @param life the life that the plant currently has.
 * @param state the state of the plant.
 */
abstract class Plant(override val position: Position,
                     override val life: Int,
                     override val state: TroopState) extends Troop :
  /**
   * The price that the player has to pay to place the plant.
   * @return the cost of the plant.
   */
  def cost: Int = costs(this)

  override def isInterestedIn: Entity => Boolean =
    case enemy: Zombie => isInMyLane(enemy) && isInRange(enemy) && isNotBehindMe(enemy)
    case _ => false

  override def collideWith(bullet: Bullet): Troop =
    val newLife: Int = Math.max(life - bullet.damage, 0)
    if newLife == 0 then this withState Dead else this withLife newLife

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Troop =
    state match
      case Idle | Attacking => if interests.isEmpty then this withState Idle else this withState Attacking
      case _ => this

  override def bullet: Bullet = bullets(this) withPosition pointOfShoot

  protected def pointOfShoot: Position = position

  private def isInMyLane(entity: Entity): Boolean = entity.position.y == position.y

  protected def isInRange(entity: Entity): Boolean = entity.position.x < position.x + range

  protected def isNotBehindMe(entity: Entity): Boolean = entity.position.x > position.x

/**
 * The Peashooter is the base plant of the game.
 *
 * @param position the position in which the plant is placed.
 * @param life the life that the plant currently has.
 * @param state the state of the plant.
 */
case class PeaShooter(override val position: Position = (0,0),
                      override val life: Int = peashooterDefaultLife,
                      override val state: TroopState = defaultPlantState) extends Plant(position, life, state):
  override def pointOfShoot: Position = (position.y, position.x.toInt + width)
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(HealthPoints: Int): Troop = copy(life = HealthPoints)
  override def withState(newState: TroopState): Troop = copy(state = newState)

/**
 * The Wallnut is a plant that can't attack any enemy but has a lot of life.
 * It's used to temporary block the wave.
 *
 * @param position the position in which the plant is placed.
 * @param life the life that the plant currently has.
 * @param state the state of the plant. Can only be 'Idle' or 'Dead'.
 */
case class Wallnut(override val position: Position = (0,0),
                   override val life: Int = wallnutDefaultLife,
                   override val state: TroopState = defaultPlantState) extends Plant(position, life, state):
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(HealthPoints: Int): Troop = copy(life = HealthPoints)
  override def withState(newState: TroopState): Troop = copy(state = newState)
  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Troop = this
  override def isInterestedIn: Entity => Boolean =
    case _ => false

case class CherryBomb(override val position: Position = (0,0),
                      override val life: Int = wallnutDefaultLife,
                      override val state: TroopState = defaultPlantState) extends Plant(position, life, state):
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(HealthPoints: Int): Troop = copy(life = HealthPoints)
  override def withState(newState: TroopState): Troop = copy(state = newState)
  override def isInterestedIn: Entity => Boolean =
    case _ => true