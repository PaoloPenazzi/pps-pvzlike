package model.entities

import BulletDefaultValues.*
import model.entities.WorldSpace.{Position, given}
import model.entities.ZombieDefaultValues.slowVelocities

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

/**
 * An [[Entity]] shoot by a [[Troop]].
 */
trait Bullet extends Entity with MovingAbility :

  /**
   * @return The damage the bullet does.
   */
  def damage: Int = BulletDefaultValues.damages(this)

  /**
   * @param entity The entity the bullet collided with.
   * @return True if the [[Bullet]] should disappear, false otherwise.
   */
  def shouldDisappearAfterHitting(entity: Entity): Boolean = true
  
  def hitMultipleTimes: Boolean = false

  /**
   * @param entity The entity with which the bullet has potentially collided
   * @return True if a collision happened, false otherwise.
   */
  def checkCollisionWith(entity: Entity): Boolean = collideWith(entity) && isInMyLane(entity)

  def applyDamage(troop: Troop): Troop =
    troop withLife (troop.life - damage)

  private def isInMyLane(entity: Entity): Boolean = entity.position.y == position.y

  protected def collideWith(entity: Entity): Boolean =
    entity.position.x <= position.x && position.x <= entity.position.x + entity.width

  override type UpdatedEntity = Bullet

  override def velocity: Float = BulletDefaultValues.velocity(this)

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Bullet =
    this withPosition newPositionAfter(elapsedTime)

  protected def newPositionAfter(elapsedTime: FiniteDuration): Position =
    (position.y, position.x + (elapsedTime.length * velocity))

/**
 * This trait models the common behaviour of the bullets shoot by the plants.
 */
trait PlantBullet extends Bullet :
  override def checkCollisionWith(entity: Entity): Boolean =
    entity match
      case _: Zombie => super.checkCollisionWith(entity)
      case _ => false

/**
 * The [[Bullet]] shoot by the [[Peashooter]]. It has no strange effects on the zombies beside dealing damage.
 *
 * @param position The initial position of the [[Bullet]].
 */
case class PeaBullet(override val position: Position = defaultBulletPosition) extends PlantBullet :
  override def withPosition(pos: Position): PeaBullet = copy(position = pos)

case class SnowBullet(override val position: Position = defaultBulletPosition) extends PlantBullet :
  override def applyDamage(troop: Troop): Troop =
    troop match
      case z: Zombie => super.applyDamage(z.withVelocity(slowVelocities(z)))
  override def withPosition(pos: Position): SnowBullet = copy(position = pos)

/**
 * The [[Bullet]] shoot by the [[CherryBomb]]. It deals damage to all [[Troop]] around his position.
 *
 * @param position The initial position of the [[Bullet]].
 */
case class CherryBullet(override val position: Position = defaultBulletPosition) extends PlantBullet :
  override def checkCollisionWith(entity: Entity): Boolean = isNearMyLane(entity) && collideWith(entity)

  override def hitMultipleTimes: Boolean = true

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Bullet = this

  override def withPosition(pos: Position): CherryBullet = copy(position = pos)

  override def collideWith(entity: Entity): Boolean = (entity.position.x - position.x).abs <= 15

  private def isNearMyLane(entity: Entity): Boolean = (entity.position.y - position.y).abs < 2

/**
 * This trait models the common behaviour of the bullets shoot by the zombies.
 */
trait ZombieBullet extends Bullet :
  override def checkCollisionWith(entity: Entity): Boolean =
    entity match
      case _: Plant => super.checkCollisionWith(entity)
      case _ => false

/**
 * The [[ZombieBullet]] shoot by the classic[[BasicZombie]] and [[FastZombie]]. It has no strange effects on the plants beside dealing damage.
 *
 * @param position The initial position of the [[Bullet]].
 */
case class PawBullet(override val position: Position = defaultBulletPosition) extends ZombieBullet :
  override def withPosition(pos: Position): Bullet = copy(position = pos)

/**
 * The [[ZombieBullet]] shoot by  [[WarriorZombie]]. It has no strange effects on the plants beside dealing damage.
 *
 * @param position The initial position of the [[Bullet]].
 */
case class SwordBullet(override val position: Position = defaultBulletPosition) extends ZombieBullet :
  override def withPosition(pos: Position): Bullet = copy(position = pos)

object Bullets:
  trait BulletBuilder[B <: Bullet]:
    def build: B

  given BulletBuilder[PeaBullet] with
    override def build: PeaBullet = PeaBullet()

  given BulletBuilder[SnowBullet] with
    override def build: SnowBullet = SnowBullet()

  given BulletBuilder[CherryBullet] with
    override def build: CherryBullet = CherryBullet()

  given BulletBuilder[PawBullet] with
    override def build: PawBullet = PawBullet()

  given BulletBuilder[SwordBullet] with
    override def build: SwordBullet = SwordBullet()

  def ofType[B <: Bullet](using bulletBuilder: BulletBuilder[B]): B =
    bulletBuilder.build

/**
 * This object contains the default values for each type of [[Bullet]].
 */
object BulletDefaultValues:
  val defaultBulletPosition: Position = (0, 0)

  /**
   * Returns the damage made by the [[Bullet]].
   */
  val damages: Bullet => Int =
    case _: CherryBullet => 1000
    case _: PeaBullet => 25
    case _: SwordBullet => 60
    case _: PawBullet => 25
    case _ => 0

  /**
   * Returns the velocity of the [[Bullet]].
   */
  val velocity: Bullet => Float =
    case _: PeaBullet => 0.06
    case _: SwordBullet => -0.1
    case _: PawBullet => -0.1
    case _: SnowBullet => 0.06
    case _ => 0