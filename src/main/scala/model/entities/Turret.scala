package model.entities

import model.common.DefaultValues.*
import model.entities.TroopState.*
import model.entities.WorldSpace.{Position, given}
import model.entities.{AttackingAbility, Bullet, Enemy, Entity, PeaBullet, Turret, Zombie}

import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

case class Turret(override val position: Position,
                          override val life: Int = 300,
                          override val state: TroopState = Idle) extends Troop :
  def cost: Int = costs(this)

  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(HPs: Int): Troop = copy(life = HPs)
  override def withState(newState: TroopState): Troop = copy(state = newState)

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

class PeaShooter(position: Position) extends Turret(position)

class Wallnut(position: Position) extends Turret(position) :
  override def isInterestedIn: Entity => Boolean =
    case _ => false

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Troop = this