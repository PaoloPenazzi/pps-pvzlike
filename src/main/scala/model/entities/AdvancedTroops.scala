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

  trait Troop[B <: Bullet] extends AdvancedEntity with AdvancedAttackingAbility:
    def bullet: B
    override def withFireRate(rate: Int): Troop[B]
    override def withSightRange(range: Int): Troop[B]

  object TroopBuilders:
    trait TroopBuilder[B <: Bullet]:
      def build(b: B): Troop[B]
    given TroopBuilder[AdvancedPeaBullet] with
      override def build(bullet: AdvancedPeaBullet): Troop[AdvancedPeaBullet] = Troop[AdvancedPeaBullet]()
    given TroopBuilder[AdvancedZombieBullet] with
      override def build(bullet: AdvancedZombieBullet): Troop[AdvancedZombieBullet] = BaseZombie[AdvancedZombieBullet]()

  import TroopBuilders.*

  def ofType[B <: Bullet](using troopBuilder: TroopBuilder[Bullet])(bullet: B): Troop[B] =
    troopBuilder.build(bullet)

  case class BasePlant[B <: Bullet]() extends Troop[Bullet]
  case class BaseZombie[B <: Bullet]() extends Troop[Bullet]