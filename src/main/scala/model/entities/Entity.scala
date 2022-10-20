package model.entities

import model.common.DefaultValues
import model.common.DefaultValues.*
import model.entities.TroopState.{Attacking, Dead, Idle}
import model.entities.WorldSpace.{Position, given}

import scala.language.implicitConversions
import scala.concurrent.duration.FiniteDuration

trait Entity:
  type UpdatedEntity <: Entity
  def width: Int = DefaultValues.width(this)
  def position: Position
  def update(elapsedTime: FiniteDuration, interest: List[Entity]): UpdatedEntity
  def isInterestedIn: Entity => Boolean = _ => false

trait MovingAbility extends Entity:
  def velocity: Float
  def updatePosition(elapsedTime: FiniteDuration): Position =
    (position.y, position.x + (elapsedTime.length * velocity))

trait AttackingAbility extends Entity:
  def bullet: Bullet
  def fireRate: Int = fireRates(this)
  def range: Int = ranges(this)
  
trait Troop extends Entity with AttackingAbility:
  override type UpdatedEntity = Troop
  def collideWith(bullet: Bullet): UpdatedEntity
  def life: Int
  def state: TroopState

object Troops:
  trait TroopBuilder[T <: Troop]:
    def build: T

  given TroopBuilder[PeaShooter] with
    override def build: PeaShooter = PeaShooter((0,0))
  given TroopBuilder[Zombie] with
    override def build: Zombie = Zombie((0,0))

  def ofType[T <: Troop](using troopBuilder: TroopBuilder[T]): T =
    troopBuilder.build

enum TroopState:
  case Idle
  case Moving
  case Attacking
  case Dead




