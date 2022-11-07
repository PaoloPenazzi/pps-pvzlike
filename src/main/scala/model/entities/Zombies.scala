package model.entities

import model.entities.ZombieDefaultValues.*
import model.entities.TroopState.*
import model.entities.WorldSpace.{LanesLength, NumOfLanes, Position}

import scala.language.implicitConversions
import scala.concurrent.duration.FiniteDuration
import scala.util.Random

/**
 * A Zombie is an abstract entity that models the common behaviour of different types of Zombie.
 *
 * @param position the position in which zombie  is placed.
 * @param life     the life that the zombie currently has.
 * @param state    the state of the zombie.
 */
trait Zombie(override val position: Position,
             override val life: Int,
             override val state: TroopState) extends Troop with MovingAbility :

  override def isInterestedIn: Entity => Boolean =
    case plant: Plant => isInMyLane(plant) && isInFrontOfMe(plant) && isInRange(plant)
    case _ => false

  private def isInMyLane(plant: Plant): Boolean = plant.position.y == position.y

  private def isInFrontOfMe(plant: Plant): Boolean = position.x > plant.position.x

  private def isInRange(plant: Plant): Boolean = position.x - plant.position.x.toInt <= range

  override def bullet: ZombieBullet = bullets(this)

  /**
   *
   * @param velocity
   * @return
   */
  def withVelocity(velocity: Float): Troop

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Troop =
    val nextState = if interests.nonEmpty then Attacking else Moving
    state match
      case Moving => (this withPosition updatePosition(elapsedTime)).withState(nextState)
      case Attacking => this withState nextState
      case _ => this

  private def updatePosition(elapsedTime: FiniteDuration): Position =
    (position.y, position.x + (elapsedTime.length * velocity))

/**
 * BasicZombie is the base enemy of the game.
 *
 * @param position the position in which BasicZombie is placed.
 * @param life     the life that BasicZombie currently has.
 * @param state    the state of BasicZombie.
 */
case class BasicZombie(override val position: Position = generateZombieSpawnPosition,
                       override val life: Int = basicZombieDefaultLife,
                       override val state: TroopState = zombieDefaultState,
                       override val velocity: Float = basicZombieDefaultVelocity) extends Zombie(position, life, state) :
  override def withPosition(pos: Position): Troop = copy(position = pos)

  override def withVelocity(vel: Float): Troop = copy(velocity = vel)

  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)

  override def withState(newState: TroopState): Troop = copy(state = newState)

/**
 * A FastZombie is a zombie that moves and attacks faster but has less life than a base zombie.
 * FastZombie can't be slowed.
 *
 * @param position the position in which the zombie is placed.
 * @param life     the life that the zombie currently has.
 * @param state    the state of the zombie.
 */
case class FastZombie(override val position: Position = generateZombieSpawnPosition,
                      override val life: Int = fastZombieDefaultLife,
                      override val state: TroopState = zombieDefaultState,
                      override val velocity: Float = fastZombieDefaultVelocity) extends Zombie(position, life, state) :
  override def withPosition(pos: Position): Troop = copy(position = pos)

  override def withVelocity(vel: Float): Troop = copy(velocity = vel)

  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)

  override def withState(newState: TroopState): Troop = copy(state = newState)


/**
 * A WarriorZombie is a zombie that moves more slowly but does a lot of damage.
 *
 * @param position the position in which the zombie is placed.
 * @param life     the life that the zombie currently has.
 * @param state    the state of the zombie.
 */
case class WarriorZombie(override val position: Position = generateZombieSpawnPosition,
                         override val life: Int = warriorZombieDefaultLife,
                         override val state: TroopState = zombieDefaultState,
                         override val velocity: Float = warriorZombieDefaultVelocity) extends Zombie(position, life, state) :
  override def withPosition(pos: Position): Troop = copy(position = pos)

  override def withVelocity(vel: Float): Troop = copy(velocity = vel)

  override def withLife(healthPoints: Int): Troop = copy(life = healthPoints)

  override def withState(newState: TroopState): Troop = copy(state = newState)


/**
 * This object contains the default values for each type of [[Zombie]].
 */
object ZombieDefaultValues:
  /**
   * Every [[Zombie]], when spawned, is in [[Moving]] state.
   */
  val zombieDefaultState: TroopState = Moving
  /**
   * The life of the [[BasicZombie]] when spawned.
   */
  val basicZombieDefaultLife: Int = 100
  /**
   * The life of the [[FastZombie]] when spawned.
   */
  val fastZombieDefaultLife: Int = 80
  /**
   * The life of the [[WarriorZombie]] when spawned.
   */
  val warriorZombieDefaultLife: Int = 200

  /**
   * Returns the [[ZombieBullet]] shoot by the [[Zombie]].
   */
  val bullets: Zombie => ZombieBullet =
    case basicZombie: BasicZombie => PawBullet(basicZombie.position)
    case fastZombie: FastZombie => PawBullet(fastZombie.position)
    case warriorZombie: WarriorZombie => SwordBullet(warriorZombie.position)

  val basicZombieDefaultVelocity: Float = -0.01
  val fastZombieDefaultVelocity: Float = -0.02
  val warriorZombieDefaultVelocity: Float = -0.008

  /**
   * Returns the velocity of [[Zombie]] when slowed.
   */
  val slowVelocities: Zombie => Float =
    case basicZombie: BasicZombie => -0.008
    case fastZombie: FastZombie => fastZombieDefaultVelocity
    case warriorZombie: WarriorZombie => -0.006

  /**
   * Generate the spawn position of Zombie
   *
   * @return the initial position of Zombie
   */
  def generateZombieSpawnPosition: Position = (Random.between(0, NumOfLanes), LanesLength + Random.between(0, 20))