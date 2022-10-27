package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}

import scala.concurrent.duration.FiniteDuration

/**
 * An [[Entity]] shoot by a [[Troop]].
 */
abstract class Bullet(position: Position) extends Entity with MovingAbility:

  /**
   * @return The damage the bullet does.
   */
  def damage: Int = damages(this)

  /**
   * @param entity The entity the bullet collided with.
   * @return True if the [[Bullet]] should disappear, false otherwise.
   */
  def shouldDisappearAfterHitting(entity: Entity): Boolean = true

  /**
   * @param entity The entity with which the bullet has potentially collided
   * @return True if a collision happened, false otherwise.
   */
  def checkCollisionWith(entity: Entity): Boolean = collideWith(entity) && isInMyLane(entity)

  private def isInMyLane(entity: Entity): Boolean = entity.position.y == position.y

  private def collideWith(entity: Entity): Boolean =
    entity.position.x <= position.x && position.x <= entity.position.x + entity.width

  override type UpdatedEntity = Bullet

  override def velocity: Float = DefaultValues.velocity(this)

  protected def updatePosition(elapsedTime: FiniteDuration): Position =
    (position.y, position.x + (elapsedTime.length * velocity))

/**
 * The [[Bullet]] shoot by the [[Peashooter]]. It has no strange effects on the zombies beside dealing damage.
 * @param position The initial position of the [[Bullet]].
 */
case class PeaBullet(override val position: Position) extends Bullet(position):
  override def checkCollisionWith(entity: Entity): Boolean =
    entity match
      case _: Enemy => super.checkCollisionWith(entity)
      case _ => false

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Bullet =
    PeaBullet(updatePosition(elapsedTime))

/**
 * The [[Bullet]] shoot by the [[Zombie]]. It has no strange effects on the plants beside dealing damage.
 * @param position The initial position of the [[Bullet]].
 */
case class PawBullet(override val position: Position) extends Bullet(position):
  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Bullet = PawBullet(updatePosition(elapsedTime))
  override def checkCollisionWith(entity: Entity): Boolean =
    entity match
      case _: Plant => super.checkCollisionWith(entity)
      case _ => false

