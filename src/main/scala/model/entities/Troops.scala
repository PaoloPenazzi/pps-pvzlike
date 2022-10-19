package model.entities

import model.entities.Troops.{BaseCake, BaseSlice}
import model.entities.Troops.TowerValues.*

import scala.language.{implicitConversions, postfixOps}

object Troops:

  trait AdvancedBullet
  trait AdvancedPeaBullet extends AdvancedBullet
  trait AdvancedZombieBullet extends AdvancedBullet

  trait AdvancedEntity

  trait AdvancedAttackingAbility:
    def fireRate: Int
    def sightRange: Int
    def withFireRate(rate: Int): AdvancedAttackingAbility
    def withSightRange(range: Int): AdvancedAttackingAbility

  trait AdvancedTroop[B <: AdvancedBullet] extends AdvancedEntity with AdvancedAttackingAbility:
    def bullet: B
    override def withFireRate(rate: Int): AdvancedTroop[B]
    override def withSightRange(range: Int): AdvancedTroop[B]

  trait TroopBuilder[B <: AdvancedTroop]:
    def build(bullet: B): AdvancedTroop[B]

  given TroopBuilder[AdvancedPeaBullet] with
    override def build(bullet: AdvancedPeaBullet): AdvancedTroop[AdvancedPeaBullet] = BasePlant[AdvancedPeaBullet](_)
  given TroopBuilder[AdvancedZombieBullet] with
    override def build(bullet: AdvancedZombieBullet): AdvancedTroop[AdvancedZombieBullet] = BaseZombie[AdvancedZombieBullet](_)

  def ofType[B <: AdvancedBullet](using troopBuilder: TroopBuilder[B])(bullet: B): AdvancedTroop[B] =
    troopBuilder.build(bullet)

  case class BasePlant[B <: AdvancedBullet](
                                             override val bullet: B,
                                             override val fireRate: Int = towerDefaultFireRatio,
                                             override val sightRange: Int = towerDefaultSightRange
                                           ) extends AdvancedTroop[AdvancedPeaBullet]:

    def this(basePlant: BasePlant[B]) = this(basePlant.bullet, basePlant.fireRate, basePlant.sightRange)
    override def withFireRate(rate: Int): AdvancedTroop[AdvancedPeaBullet] = copy(fireRate = rate)
    override def withSightRange(range: Int): AdvancedTroop[AdvancedPeaBullet] = copy(sightRange = range)


  case class BaseZombie[B <: AdvancedBullet](
                                              override val bullet: B,
                                              override val fireRate: Int = towerDefaultFireRatio,
                                              override val sightRange: Int = towerDefaultSightRange
                                            ) extends AdvancedTroop[AdvancedZombieBullet]:

    // def this(baseZombie: BaseZombie[B]) = this(baseZombie.bullet, baseZombie.fireRate, baseZombie.sightRange)
    override def withFireRate(rate: Int): AdvancedTroop[AdvancedZombieBullet] = copy(fireRate = rate)
    override def withSightRange(range: Int): AdvancedTroop[AdvancedZombieBullet] = copy(sightRange = range)

  object TowerValues:
    val fireRates: AdvancedBullet => Int = {
      case _: AdvancedPeaBullet => 2
      case _: AdvancedZombieBullet => 3
      case _ => 1
    }

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
    builder.build[B]

@main
def test(): Unit =
  val newSlice: BaseSlice = Troops of BaseSlice()
  val newCake: BaseCake = Troops of BaseCake()*/

