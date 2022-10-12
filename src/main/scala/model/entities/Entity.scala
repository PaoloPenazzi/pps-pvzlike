package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*
import model.entities.WorldSpace.{Position, given}

import scala.concurrent.duration.FiniteDuration

trait Entity:
  type UpdatedEntity <: Entity
  def width: Int = DefaultValues.width(this)
  def position: Position
  def update(elapsedTime: FiniteDuration, interest: List[Entity]): UpdatedEntity
  def isInterestedIn: Entity => Boolean = _ => false

trait MovingEntity() extends Entity:
  def velocity: Float
  def updatePosition(elapsedTime: FiniteDuration): Position =
    (position.y, position.x + (elapsedTime.length * velocity))
  
trait Troop extends Entity:
  def updateAfterCollision(entity: Entity): UpdatedEntity
  def life: Int
  
trait AttackingEntity extends Troop :
  /**
   * filters to keep only all game entities present on the same lane
   * @return true if it is of his own interest
   */
  def getBullet: Bullet
  def canAttack(entity: Entity): Boolean
  def fireRate: Int = fireRates(this)
  def range: Int = ranges(this)