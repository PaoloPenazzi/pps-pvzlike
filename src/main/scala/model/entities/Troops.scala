package model.entities

import model.entities.Troops.{AdvancedPeaBullet, AdvancedPeashooter}
import model.entities.Troops.TowerValues.*

import scala.language.{implicitConversions, postfixOps}

object Troops:

  trait AdvancedBullet
  case class AdvancedPeaBullet() extends AdvancedBullet
  case class AdvancedZombieBullet() extends AdvancedBullet

  trait AdvancedEntity

  trait AdvancedAttackingAbility:
    def fireRate: Int
    def sightRange: Int
    def life: Int
    def withFireRate(rate: Int): AdvancedAttackingAbility
    def withSightRange(range: Int): AdvancedAttackingAbility
    def withLife(hp: Int): AdvancedAttackingAbility

  trait AdvancedTroop[B <: AdvancedBullet] extends AdvancedEntity with AdvancedAttackingAbility:
    def bullet: B
    override def withFireRate(rate: Int): AdvancedTroop[B]
    override def withSightRange(range: Int): AdvancedTroop[B]
    override def withLife(hp: Int): AdvancedTroop[B]

  trait TroopBuilder[B <: AdvancedBullet]:
    def build(bullet: B): AdvancedTroop[B]

  given TroopBuilder[AdvancedPeaBullet] with
    override def build(bullet: AdvancedPeaBullet): AdvancedPeashooter[AdvancedPeaBullet] = AdvancedPeashooter[AdvancedPeaBullet]()
  given TroopBuilder[AdvancedZombieBullet] with
    override def build(bullet: AdvancedZombieBullet): BaseZombie[AdvancedZombieBullet] = BaseZombie[AdvancedZombieBullet]()

  def shooting[B <: AdvancedBullet](bullet: B)(using troopBuilder: TroopBuilder[B]): AdvancedTroop[B] =
    troopBuilder.build(bullet)

  case class AdvancedPeashooter[B <: AdvancedBullet](
                                             override val fireRate: Int = towerDefaultFireRatio,
                                             override val sightRange: Int = towerDefaultSightRange,
                                             override val life: Int = towerDefaultLife
                                           ) extends AdvancedTroop[AdvancedPeaBullet]:

    override def bullet: AdvancedPeaBullet = ???
    override def withFireRate(rate: Int): AdvancedTroop[AdvancedPeaBullet] = copy(fireRate = rate)
    override def withSightRange(range: Int): AdvancedTroop[AdvancedPeaBullet] = copy(sightRange = range)
    override def withLife(hp: Int): AdvancedTroop[AdvancedPeaBullet] = copy(life = hp)

  case class BaseZombie[B <: AdvancedBullet](
                                              override val fireRate: Int = towerDefaultFireRatio,
                                              override val sightRange: Int = towerDefaultSightRange,
                                              override val life: Int = towerDefaultLife
                                            ) extends AdvancedTroop[AdvancedZombieBullet]:

    override def bullet: AdvancedZombieBullet = ???
    override def withFireRate(rate: Int): AdvancedTroop[AdvancedZombieBullet] = copy(fireRate = rate)
    override def withSightRange(range: Int): AdvancedTroop[AdvancedZombieBullet] = copy(sightRange = range)
    override def withLife(hp: Int): AdvancedTroop[AdvancedZombieBullet] = copy(life = hp)

  object TowerValues:
    val fireRates: AdvancedBullet => Int = {
      case _: AdvancedPeaBullet => 2
      case _: AdvancedZombieBullet => 3
      case _ => 1
    }
    val towerDefaultLife: Int = 100
    val towerDefaultFireRatio: Int = 1
    val towerDefaultSightRange: Int = 75


/*  trait Apple
  trait Slice extends Apple
  trait Cake extends Apple
  case class BaseSlice() extends Slice
  case class BaseCake() extends Cake

  trait AppleBuilder[B <: Apple]:
    def build[B]: B
  given AppleBuilder[BaseSlice] with
    override def build(): Slice = BaseSlice()
  given AppleBuilder[BaseCake] with
    override def build(): Cake = BaseCake()

  def of[B <: Apple](impl: B)(using builder: AppleBuilder[B]): B =
    builder.build[B]*/

@main
def test(): Unit =
  val plant = Troops shooting AdvancedPeaBullet() withLife 200

