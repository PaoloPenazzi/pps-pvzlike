package model.entities

//import model.entities.Troops.{AdvancedPeaBullet, AdvancedPeashooter, BaseCake}
//import model.entities.Troops.TowerValues.*

import scala.language.{implicitConversions, postfixOps}

object Troops:
  trait Apple
  /*trait AdvancedBullet
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

  trait AdvancedTroop extends AdvancedEntity with AdvancedAttackingAbility:
    def bullet
    override def withFireRate(rate: Int): AdvancedTroop
    override def withSightRange(range: Int): AdvancedTroop
    override def withLife(hp: Int): AdvancedTroop

  case class BasicTroop(override val life: Int = towerDefaultLife) extends AdvancedTroop:
    override def withLife(hp: Int): AdvancedTroop = copy(life = hp)

  trait TroopBuilder[B <: AdvancedBullet]:
    def build(bullet: B): AdvancedTroop

  given TroopBuilder[AdvancedPeaBullet] with
    override def build(bullet: AdvancedPeaBullet): AdvancedPeashooter[AdvancedPeaBullet] = AdvancedPeashooter[AdvancedPeaBullet]()
  given TroopBuilder[AdvancedZombieBullet] with
    override def build(bullet: AdvancedZombieBullet): BaseZombie[AdvancedZombieBullet] = BaseZombie[AdvancedZombieBullet]()

  def shooting[B <: AdvancedBullet](bullet: B)(using troopBuilder: TroopBuilder[B]): AdvancedTroop[B] =
    troopBuilder.build(bullet)

  case class AdvancedPeashooter(override val fireRate: Int = towerDefaultFireRatio,
                                override val sightRange: Int = towerDefaultSightRange
                               ) extends AdvancedTroop[AdvancedPeaBullet]:

    override def bullet: AdvancedPeaBullet = ???
    override def withFireRate(rate: Int): AdvancedTroop = copy(fireRate = rate)
    override def withSightRange(range: Int): AdvancedTroop = copy(sightRange = range)


  case class BaseZombie[B <: AdvancedBullet](
                                              override val fireRate: Int = towerDefaultFireRatio,
                                              override val sightRange: Int = towerDefaultSightRange,
                                              override val life: Int = towerDefaultLife
                                            ) extends AdvancedTroop[AdvancedZombieBullet]:

    override def bullet: AdvancedZombieBullet = ???
    override def withFireRate(rate: Int): AdvancedTroop = copy(fireRate = rate)
    override def withSightRange(range: Int): AdvancedTroop = copy(sightRange = range)
    override def withLife(hp: Int): AdvancedTroop = copy(life = hp)

  object TowerValues:
    val fireRates: AdvancedBullet => Int = {
      case _: AdvancedPeaBullet => 2
      case _: AdvancedZombieBullet => 3
      case _ => 1
    }
    val towerDefaultLife: Int = 100
    val towerDefaultFireRatio: Int = 1
    val towerDefaultSightRange: Int = 75*/

/*
  trait Apple
  trait Slice extends Apple
  trait Cake extends Apple
  case class BaseSlice() extends Slice
  case class BaseCake() extends Cake

  trait AppleBuilder[B <: Apple]:
    def build: B

  given AppleBuilder[BaseSlice] with
    override def build: BaseSlice = BaseSlice()
  given AppleBuilder[BaseCake] with
    override def build: BaseCake = BaseCake()

  def of[B <: Apple](using builder: AppleBuilder[B]): B =
    builder.build

@main
def test(): Unit =
  val test: Apple = Troops.of[BaseCake]*/

