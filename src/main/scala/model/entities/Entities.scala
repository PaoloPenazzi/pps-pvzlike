package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*
import model.entities.TroopState.{Attacking, Dead, Idle}
import model.entities.WorldSpace.{CellLength, Position, given}

import scala.language.implicitConversions
import scala.concurrent.duration.FiniteDuration

/**
 * Basic entity of the game.
 */
trait Entity:
  /**
   * The type that the [[update()]] method of an [[Entity]] should return.
   */
  type UpdatedEntity <: Entity


  /**
   * @param pos The new position of the [[Entity]].
   * @return The same [[Entity]] with the position updated.
   */
  def withPosition(pos: Position): UpdatedEntity

  /**
   * @return the width of the [[Entity]].
   */
  def width: Float = EntityDefaultValues.width(this)

  /**
   * @return The current position of the [[Entity]].
   */
  def position: Position

  /**
   * @param elapsedTime The time elapsed from the last update.
   * @param interest    The list of [[Entity]] with whom it interacts.
   * @return The updated [[Entity]].
   */
  def update(elapsedTime: FiniteDuration, interest: List[Entity]): UpdatedEntity

  /**
   * The predicate used to filter which [[Entity]] is interested to
   *
   * @return True if it is interested to the [[Entity]], false otherwise.
   */
  def isInterestedIn: Entity => Boolean = _ => false

/**
 * Add the ability to move to an [[Entity]].
 */
trait MovingAbility extends Entity :
  /**
   * @return The velocity of the [[Entity]].
   */
  def velocity: Float


/**
 * Add the ability to attack to an [[Entity]].
 */
trait AttackingAbility extends Entity :

  /**
   * @return The [[Bullet]] that the [[Entity]] shoots.
   */
  def bullet: Bullet

  /**
   * @return The rate of fire of the [[Entity]].
   */
  def fireRate: Int = EntityDefaultValues.fireRates(this)

  /**
   * @return the sighting range of the [[Entity]].
   */
  def range: Int = EntityDefaultValues.ranges(this)


/**
 * This object contains the default values for each type of [[Entity]].
 */
object EntityDefaultValues:
  /**
   * Given an [[Entity]] with [[AttackingAbility]], it returns the fireRate of the entity.
   */
  val fireRates: AttackingAbility => Int =
    case _: Shooter[_] => 2
    case _: CherryBomb => 1
    case _: BasicZombie => 3
    case _: FastZombie => 2
    case _: WarriorZombie => 4
    case _ => 0

  /**
   * Given an [[Entity]] with [[AttackingAbility]], it returns the range of the entity.
   */
  val ranges: AttackingAbility => Int =
    case _: Shooter[_] => 80
    case _: CherryBomb => 1000
    case _: BasicZombie => 10
    case _: FastZombie => 15
    case _: WarriorZombie => 10
    case _ => 0

  /**
   * Given an [[Entity]] it returns its width.
   */
  val width: Entity => Float =
    case _: CherryBullet => CellLength * 2
    case _: Troop => CellLength / 1.5.toFloat
    case _: Bullet => CellLength / 4
    case _ => 0