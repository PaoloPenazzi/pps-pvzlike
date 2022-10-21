package model.entities

import model.common.DefaultValues.*
import model.entities.TroopState.*
import model.entities.WorldSpace.{Position, given}
import model.entities.{AttackingAbility, Bullet, Enemy, Entity, PeaBullet, Turret, Zombie}

import scala.concurrent.duration.FiniteDuration

trait Turret() extends Troop:
  def cost: Int = costs(this)

  override def isInterestedIn: Entity => Boolean =
    case enemy: Enemy => enemy.position.y == position.y
    case _ => false

case class PeaShooter(override val position: Position,
                      override val life: Int = 300,
                      override val state: TroopState = Idle) extends Turret :
  
  override def withPosition(pos: Position): Troop = copy(position = pos)
  override def withLife(HPs: Int): Troop = copy(life = HPs)
  override def withState(newState: TroopState): Troop = copy(state = newState)

  override def collideWith(bullet: Bullet): Troop =
    val newLife: Int = Math.max(life - bullet.damage, 0)
    if newLife == 0 then this withState Dead else this


  override def bullet: Bullet = new PeaBullet(position)

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Troop =
    state match
      case Idle | Attacking =>
        if interests.isEmpty then this withState Idle else this withState Attacking
      case _ => this