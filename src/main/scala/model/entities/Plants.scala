package model.entities

import model.common.DefaultValues.*
import PlantDefaultValues.*
import model.entities.TroopState.*
import model.entities.WorldSpace.{Position, given}
import model.entities.{AttackingAbility, Bullet, Zombie, Entity, PeaBullet, Plant, BasicZombie}

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

/**
 * A plant is a trait that models the common behaviour of different types of plants.
 */
trait Plant extends Troop :
  /**
   * The price that the player has to pay to place the plant.
   *
   * @return the cost of the plant.
   */
  def cost: Int = PlantDefaultValues.costs(this)

  override def bullet: Bullet = PlantDefaultValues.bullets(this) withPosition pointOfShoot

  override def pointOfShoot: Position = position

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

  protected def pointOfShoot: Position = position

  protected def isInRange(entity: Entity): Boolean = entity.position.x < position.x + range

  protected def isNotBehindMe(entity: Entity): Boolean = entity.position.x > position.x

  private def isInMyLane(entity: Entity): Boolean = entity.position.y == position.y


/**
 * The Peashooter is a plant that attacks zombies.
 *
 * @param position the position in which the plant is placed.
 * @param life     the life that the plant currently has.
 * @param state    the state of the plant.
 */

case class PeaShooter(override val position: Position = (0, 0),
                      override val life: Int = peashooterDefaultLife,
                      override val state: TroopState = defaultPlantState) extends Plant :

  override def pointOfShoot: Position = (position.y, position.x.toInt + width)

  override def withPosition(pos: Position): Troop = copy(position = pos)

  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)

  override def withState(newState: TroopState): Troop = copy(state = newState)

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Troop =
    state match
      case Idle | Attacking => if interests.isEmpty then this withState Idle else this withState Attacking
      case _ => this


/**
 * The Wallnut is a plant that can't attack any enemy but has a lot of life.
 * It's used to temporary block the wave.
 *
 * @param position the position in which the plant is placed.
 * @param life     the life that the plant currently has.
 * @param state    the state of the plant. Can only be 'Idle' or 'Dead'.
 */
case class Wallnut(override val position: Position = (0, 0),
                   override val life: Int = wallnutDefaultLife,
                   override val state: TroopState = defaultPlantState) extends Plant :

  override def withPosition(pos: Position): Troop = copy(position = pos)

  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)

  override def withState(newState: TroopState): Troop = copy(state = newState)

  override def isInterestedIn: Entity => Boolean =
    case _ => false

/**
 * The CherryBomb is a [[Plant]] that when placed, explode after a short time.
 *
 * @param position the position in which the plant is placed.
 * @param life     the life that the plant currently has.
 * @param state    the state of the plant.
 */
case class CherryBomb(override val position: Position = (0, 0),
                      override val life: Int = cherryBombDefaultLife,
                      override val state: TroopState = defaultPlantState) extends Plant :

  override def withPosition(pos: Position): Troop = copy(position = pos)

  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)

  override def withState(newState: TroopState): Troop = copy(state = newState)

  override def isInterestedIn: Entity => Boolean =
    case _ => true

/**
 * This object contains the default values for each type of [[Plant]].
 */
object PlantDefaultValues:
  /**
   * Every [[Plant]], when spawned, is in [[Idle]] state.
   */
  val defaultPlantState: TroopState = TroopState.Idle
  /**
   * The life of the [[CherryBomb]] when spawned.
   */
  val cherryBombDefaultLife: Int = 10
  /**
   * The life of the [[PeaShooter]] when spawned.
   */
  val peashooterDefaultLife: Int = 100
  /**
   * The life of the [[Wallnut]] when spawned.
   */
  val wallnutDefaultLife: Int = 150
  /**
   * Returns the [[PlantBullet]] shoot by the [[Plant]].
   */
  val bullets: Plant => PlantBullet =
    case p: PeaShooter => PeaBullet(p.pointOfShoot)
    case c: CherryBomb => CherryBullet(c.position)
  /**
   * Returns the cost of the [[Plant]].
   */
  val costs: Plant => Int =
    case _: PeaShooter => 100
    case _: Wallnut => 50
    case _: CherryBomb => 150
    case _ => 1000
>>>>>>> main
