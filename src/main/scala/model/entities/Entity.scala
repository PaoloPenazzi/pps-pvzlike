package model.entities

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
   * @return the width of the [[Entity]].
   */
  def width: Float = EntityDefaultValues.width(this)

  /**
   * @return The current position of the [[Entity]].
   */
  def position: Position

  /**
   * @param pos The new position of the [[Entity]].
   * @return The same [[Entity]] with the position updated.
   */
  def withPosition(pos: Position): UpdatedEntity


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
 * A troop is an [[Entity]] that is either a [[BasicZombie]] or a [[Plant]].
 */
trait Troop extends Entity with AttackingAbility :
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
   * @param healthPoints The new life of the [[Troop]].
   * @return The same [[Troop]] with the life updated.
   */
  def withLife(healthPoints: Int): Troop

object Troops:
  /**
   * A builder used to create [[Troop]].
   *
   * @tparam T The type of the [[Troop]].
   */
  trait TroopBuilder[T <: Troop]:
    /**
     * @return A [[Troop]] of type [[T]]
     */
    def build: T

  /**
   * Given instances to create [[Troop]] depending on the type.
   */
  given TroopBuilder[PeaShooter] with
    override def build: PeaShooter = PeaShooter()

  given TroopBuilder[BasicZombie] with
    override def build: BasicZombie = BasicZombie()

  given TroopBuilder[FastZombie] with
    override def build: FastZombie = FastZombie()

  given TroopBuilder[WarriorZombie] with
    override def build: WarriorZombie = WarriorZombie()

  given TroopBuilder[Wallnut] with
    override def build: Wallnut = Wallnut()

  given TroopBuilder[CherryBomb] with
    override def build: CherryBomb = CherryBomb()

  /**
   * A DSL method to create [[Troop]].
   *
   * @param troopBuilder The [[TroopBuilder]] of the type needed.
   * @tparam T The [[Troop]] type.
   * @return The [[Troop]] of the specified type with position (0,0).
   */
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

/**
 * This object contains the default values for each type of [[Entity]].
 */
object EntityDefaultValues:
  /**
   * Given an [[Entity]] with [[AttackingAbility]], it returns the fireRate of the entity.
   */
  val fireRates: AttackingAbility => Int =
    case _: PeaShooter => 2
    case _: CherryBomb => 1
    case _: BasicZombie => 3
    case _: FastZombie => 2
    case _: WarriorZombie => 4
    case _ => 0

  /**
   * Given an [[Entity]] with [[AttackingAbility]], it returns the range of the entity.
   */
  val ranges: AttackingAbility => Int =
    case _: PeaShooter => 80
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