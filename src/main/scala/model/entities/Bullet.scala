package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}

import scala.concurrent.duration.FiniteDuration

/**
 * An [[Entity]] shoot by a [[Troop]].
 */
trait Bullet extends Entity with MovingAbility:

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

  override type UpdatedEntity = Bullet
  override def velocity: Float = DefaultValues.velocity(this)
  private def isInMyLane(entity: Entity): Boolean = entity.position.y == position.y
  private def collideWith(entity: Entity): Boolean =
    entity.position.x <= position.x && position.x <= entity.position.x + entity.width

/**
 * The [[Bullet]] shoot by the [[Peashooter]]. It has no strange effects on the zombies beside dealing damage.
 * @param position The initial position of the [[Bullet]].
 */
class PeaBullet(override val position: Position) extends Bullet:
  override def checkCollisionWith(entity: Entity): Boolean =
    entity match
      case _: Enemy => super.checkCollisionWith(entity)
      case _ => false

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Bullet =
    PeaBullet(updatePosition(elapsedTime))

  private def updatePosition(elapsedTime: FiniteDuration): Position =
    (position.y, position.x + (elapsedTime.length * velocity))

/**
 * The [[Bullet]] shoot by the [[Zombie]]. It has no strange effects on the plants beside dealing damage.
 * @param position The initial position of the [[Bullet]].
 */
class Paw(override val position: Position) extends Bullet:
  override def velocity: Float = -0.1

  override def checkCollisionWith(entity: Entity): Boolean =
    entity match
      case _: Plant => super.checkCollisionWith(entity)
      case _ => false

  override def update(elapsedTime: FiniteDuration, interests: List[Entity]): Bullet =
    Paw(updatePosition(elapsedTime))

  private def updatePosition(elapsedTime: FiniteDuration): Position =
    (position.y, position.x + (elapsedTime.length * velocity))


