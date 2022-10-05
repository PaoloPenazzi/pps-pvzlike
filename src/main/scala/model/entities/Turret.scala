package model.entities

import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}
import model.entities.{AttackingEntity, Bullet, Enemy, Entity, Seed, Turret, Zombie}

import scala.concurrent.duration.FiniteDuration

trait Turret extends Entity with AttackingEntity:
  override type UpdatedEntity = Turret
  def cost: Int = costs(this)

  def canAttack(enemy: Enemy): Boolean =
    isOnMyPath(enemy) && isInRange(enemy)

  private def isOnMyPath(enemy: Enemy): Boolean =
    enemy.position.y == position.y

  private def isInRange(enemy: Enemy): Boolean =
    enemy.position.x.toInt <= range

  override def filter: Entity => Boolean =
      case enemy: Enemy => enemy.position.y == position.y
      case _ => false

/**
 * Basic turret.
 *
 * @param position The position in which the plant is placed by the player.
 */
case class Plant(override val position: Position) extends Turret:
  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Plant =
    Plant(position)