package model.entities

import model.common.DefaultValues.*
import model.entities.TroopState.*
import model.entities.WorldSpace.{Position, given}
import model.entities.{AttackingAbility, Bullet, Enemy, Entity, PeaBullet, Turret, Zombie}

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

/**
 * A turret is an abstract entity that models the common behaviour of different types of turrets.
 *
 * @param position the position in which the turret is placed.
 * @param life the life that the turret currently has.
 * @param state the state of the turret.
 */
abstract class Turret(override val position: Position,
                          override val life: Int,
                          override val state: TroopState) extends Troop :
  /**
   * The price that the player has to pay to place the turret.
   * @return the cost of the turret.
   */
  def cost: Int = costs(this)

  override def isInterestedIn: Entity => Boolean =
    case enemy: Enemy => enemy.position.y == position.y
    case _ => false

  override def collideWith(bullet: Bullet): Troop =
    val newLife: Int = Math.max(life - bullet.damage, 0)
    if newLife == 0 then this withState Dead else this

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Troop =
    state match
      case Idle | Attacking => if interests.isEmpty then this withState Idle else this withState Attacking
      case _ => this

  override def bullet: Bullet = bullets(this)

/**
 * The Peashooter is the base turret of the game.
 *
 * @param position the position in which the turret is placed.
 * @param life the life that the turret currently has.
 * @param state the state of the turret.
 */
case class PeaShooter(override val position: Position,
                      override val life: Int = peashooterDefaultLife,
                      override val state: TroopState = defaultTurretState) extends Turret(position, life, state):
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(HPs: Int): Troop = copy(life = HPs)
  override def withState(newState: TroopState): Troop = copy(state = newState)

/**
 * The Wallnut is a turret that can't attack any enemy but has a lot of life.
 * It's used to temporary block the wave.
 *
 * @param position the position in which the turret is placed.
 * @param life the life that the turret currently has.
 * @param state the state of the turret. Can only be 'Idle' or 'Dead'.
 */
case class Wallnut(override val position: Position,
                   override val life: Int = wallnutDeafultLife,
                   override val state: TroopState = defaultTurretState) extends Turret(position, life, state):
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(HPs: Int): Troop = copy(life = HPs)
  override def withState(newState: TroopState): Troop = copy(state = newState)

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Troop = this

  override def isInterestedIn: Entity => Boolean =
    case _ => false
