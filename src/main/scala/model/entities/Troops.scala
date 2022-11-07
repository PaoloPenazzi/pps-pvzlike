package model.entities

import model.entities.Bullets.BulletBuilder
import model.entities.WorldSpace.{Position, given}
import TroopState.*
import scala.language.implicitConversions

/**
 * A troop is an [[Entity]] that is either a [[BasicZombie]] or a [[Plant]].
 */
trait Troop extends Entity with AttackingAbility :
  override type UpdatedEntity = Troop

  /**
   * @param bullet The [[Bullet]] that hit the [[Troop]].
   * @return The Entity updated after the collision.
   */
  def collideWith(bullet: Bullet): Troop =
    val troop = bullet applyDamage this
    if troop.life <= 0 then troop withState Dead else troop

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
  trait TroopBuilder[T <: Troop, B <: Bullet]:
    /**
     * @return A [[Troop]] of type [[T]]
     */
    def build: T

  /**
   * Given instances to create [[Troop]] depending on the type.
   */
  given TroopBuilder[BasicZombie, Bullet] with
    override def build: BasicZombie = BasicZombie()

  given TroopBuilder[FastZombie, Bullet] with
    override def build: FastZombie = FastZombie()

  given TroopBuilder[WarriorZombie, Bullet] with
    override def build: WarriorZombie = WarriorZombie()

  given TroopBuilder[Wallnut, Bullet] with
    override def build: Wallnut = Wallnut()

  given TroopBuilder[CherryBomb, Bullet] with
    override def build: CherryBomb = CherryBomb()

  given TroopBuilder[Shooter[PeaBullet], PeaBullet] with
    override def build: Shooter[PeaBullet] = Shooter[PeaBullet](Bullets.ofType[PeaBullet])

  given TroopBuilder[Shooter[SnowBullet], SnowBullet] with
    override def build: Shooter[SnowBullet] = Shooter[SnowBullet](Bullets.ofType[SnowBullet])

  /**
   * A DSL method to create [[Troop]].
   *
   * @param troopBuilder The [[TroopBuilder]] of the type needed.
   * @tparam T The [[Troop]] type.
   * @return The [[Troop]] of the specified type with position (0,0).
   */
  def ofType[T <: Troop](using troopBuilder: TroopBuilder[T, Bullet]): T =
    troopBuilder.build

  def shooterOf[B <: Bullet](using troopBuilder: TroopBuilder[Shooter[B], B]): Shooter[B] =
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

