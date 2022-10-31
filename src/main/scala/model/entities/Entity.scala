package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*
import model.entities.TroopState.{Attacking, Dead, Idle}
import model.entities.WorldSpace.{Position, given}

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
  
  def width: Int = DefaultValues.width(this)

  /**
   * @return The current position of the [[Entity]].
   */
  def position: Position

  /**
   * @param elapsedTime The time elapsed from the last update.
   * @param interest The list of [[Entity]] with whom it interacts.
   * @return The updated [[Entity]].
   */
  def update(elapsedTime: FiniteDuration, interest: List[Entity]): UpdatedEntity

  /**
   * The predicate used to filter which [[Entity]] is interested to
   * @return True if it is interested to the [[Entity]], false otherwise.
   */
  def isInterestedIn: Entity => Boolean = _ => false

/**
 * Add the ability to move to an [[Entity]].
 */
trait MovingAbility extends Entity:
  /**
   * @return The velocity of the [[Entity]].
   */
  def velocity: Float

  

/**
 * Add the ability to attack to an [[Entity]].
 */
trait AttackingAbility extends Entity:
  /**
   * @return The position in which the bullet should be spawned.
   */
  def pointOfShoot: Position

  /**
   * @return The [[Bullet]] that the [[Entity]] shoots.
   */
  def bullet: Bullet

  /**
   * @return The rate of fire of the [[Entity]].
   */
  def fireRate: Int = fireRates(this)

  /**
   * @return the sighting range of the [[Entity]].
   */
  def range: Int = ranges(this)

/**
 * A troop is an [[Entity]] that is either a [[Zombie]] or a [[Plant]].
 */
trait Troop extends Entity with AttackingAbility:
  override type UpdatedEntity = Troop

  /**
   * @param bullet The [[Bullet]] that hit the [[Troop]].
   * @return The Entity updated after the collision.
   */
  def collideWith(bullet: Bullet): UpdatedEntity

  /**
   * @return The current life of the [[Troop]].
   */
  def life: Int

  /**
   * @return The current state of the [[Troop]]. The possible returned values can be found in [[TroopState]].
   */
  def state: TroopState

  /**
   * @param newState The new [[TroopState]] of the [[Troop]].
   * @return The same [[Troop]] with the state updated.
   */
  def withState(newState: TroopState): Troop

  /**
   * @param HealthPoints The new life of the [[Troop]].
   * @return The same [[Troop]] with the life updated.
   */
  def withLife(HealthPoints: Int): Troop

  /**
   * @param pos The new position of the [[Troop]].
   * @return The same [[Troop]] with the position updated.
   */
  def withPosition(pos: Position): Troop

object Troops:
  /**
   * A builder used to create [[Troop]].
   * @tparam T The type of the [[Troop]].
   */
  trait TroopBuilder[T <: Troop]:
    /**
     * @return A [[Troop]] of type [[T]]
     */
    def build: T

  trait ShooterBuilder[B <: Bullet]:
    def build: Troop
  given ShooterBuilder[PeaBullet]  with
    override def build: Troop = Shooter[PeaBullet](PeaBullet(0,0))
  given ShooterBuilder[SnowBullet] with
    override def build: Troop = Shooter[SnowBullet](SnowBullet(0,0))
  /**
   * A given instance to create [[Troop]] depending on the type.
   */
  /*given TroopBuilder[PeaShooter] with
    override def build: PeaShooter = PeaShooter()*/
  /*given TroopBuilder[Shooter[PeaBullet]] with
    override def build: Shooter[PeaBullet] = Shooter()
  given TroopBuilder[Shooter[SnowBullet]] with
    override def build: Shooter[SnowBullet] = Shooter()*/
  given TroopBuilder[Zombie] with
    override def build: Zombie = Zombie()
  given TroopBuilder[Wallnut] with
    override def build: Wallnut = Wallnut()

  /**
   * A DSL method to create [[Troop]].
   *
   * @param troopBuilder The [[TroopBuilder]] of the type needed.
   * @tparam T The [[Troop]] type.
   * @return The [[Troop]] of the specified type with position (0,0).
   */
  def thatShoot[B <: Bullet](using shooterBuilder: ShooterBuilder[B]): Troop =
    shooterBuilder.build
  def ofType[T <: Troop](using troopBuilder: TroopBuilder[T]): T =
    troopBuilder.build

/**
 * An Enumeration that models every possible [[Troop]] state.
 */
enum TroopState:
  /**
   * The [[Troop]] is doing nothing, wait for something to happen.
   */
  case Idle

  /**
   * The [[Troop]] is moving.
   */
  case Moving

  /**
   * The [[Troop]] is attacking another [[Troop]].
   */
  case Attacking

  /**
   * The [[Troop]] is dead.
   */
  case Dead




