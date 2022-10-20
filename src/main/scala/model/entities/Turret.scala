package model.entities

import model.common.DefaultValues.*
import model.entities.TroopState.*
import model.entities.WorldSpace.{Position, given}
import model.entities.{AttackingAbility, Bullet, Enemy, Entity, PeaBullet, Turret, Zombie}

import scala.concurrent.duration.FiniteDuration

trait Turret extends Troop:
  def cost: Int = costs(this)

  override def isInterestedIn: Entity => Boolean =
    case enemy: Enemy => enemy.position.y == position.y
    case _ => false

case class PeaShooter(override val position: Position,
                      override val life: Int = 300,
                      override val state: TroopState = Idle) extends Turret :

  override def collideWith(bullet: Bullet): Turret =
    val newLife: Int = Math.max(life - bullet.damage, 0)
    PeaShooter(position, newLife, if newLife == 0 then Dead else state)

  override def bullet: Bullet = new PeaBullet(position)

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): PeaShooter =
    state match
      case Idle | Attacking => PeaShooter(
        position,
        life,
        if interests.isEmpty then Idle else Attacking)
      case _ => this