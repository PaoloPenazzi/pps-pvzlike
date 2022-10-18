package model.entities

import model.entities.Troops.TowerValues.*
import scala.language.implicitConversions

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

  trait TroopBuilder[B <: AdvancedBullet]:
    def build(bullet: B): AdvancedTroop[B]
  given TroopBuilder[AdvancedPeaBullet] with
    override def build(bullet: AdvancedPeaBullet): AdvancedTroop[AdvancedPeaBullet] = BasePlant[AdvancedPeaBullet]()
  given TroopBuilder[AdvancedZombieBullet] with
    override def build(bullet: AdvancedZombieBullet): AdvancedTroop[AdvancedZombieBullet] = BaseZombie[AdvancedZombieBullet]()

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
    def this(baseZombie: BaseZombie[B]) = this(baseZombie.bullet, baseZombie.fireRate, baseZombie.sightRange)
    override def withFireRate(rate: Int): AdvancedTroop[AdvancedZombieBullet] = copy(fireRate = rate)
    override def withSightRange(range: Int): AdvancedTroop[AdvancedZombieBullet] = copy(sightRange = range)

  object TowerValues:
    val fireRates: Bullet => Int = {
      case _: AdvancedPeaBullet => 2
      case _: AdvancedZombieBullet => 3
      case _ => 1
    }

    val towerDefaultFireRatio: Int = 1
    val towerDefaultSightRange: Int = 75

  // Troops ofType Peashooter inPosition (20,2)