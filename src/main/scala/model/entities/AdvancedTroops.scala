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

  
