package model.entities

import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}
import model.entities.{AttackingEntity, Bullet, Enemy, Entity, Seed, Turret, Zombie}

import scala.concurrent.duration.FiniteDuration

trait Turret extends Entity with AttackingEntity with Troop:
  override type UpdatedEntity = Turret
  def cost: Int = costs(this)


  override def interest: Entity => Boolean =
      case enemy: Enemy => enemy.position.y == position.y
      case _ => false

/**
 * Basic turret.
 *
 * @param position The position in which the plant is placed by the player.
 */
case class Plant(override val position: Position, override val life: Int = 300) extends Turret:
  override def canAttack(enemy: Entity): Boolean =
    enemy.position.x.toInt <= range

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Plant =
    Plant(position)