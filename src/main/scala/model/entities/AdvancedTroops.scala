package model.entities

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

  case class BasePlant[B <: AdvancedBullet]() extends AdvancedTroop[AdvancedPeaBullet]

  case class BaseZombie[B <: AdvancedBullet]() extends AdvancedTroop[AdvancedZombieBullet]

  object TowerValues:
    val sightRanges: Bullet => Int = {
      case _: AdvancedPeaBullet => 100
      case _: AdvancedZombieBullet => 10
      case _ => 50
    }

    val fireRates: Bullet => Int = {
      case _: AdvancedPeaBullet => 2
      case _: AdvancedZombieBullet => 3
      case _ => 1
    }

    val towerDefaultShotRatio: Int = 1
    val towerDefaultSightRange: Int = 75

  // Troops ofType Peashooter inPosition (20,2)