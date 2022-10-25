package model.common

import model.entities.*
import scala.language.implicitConversions

object DefaultValues:
  val defaultPlantState: TroopState = TroopState.Idle
  val peashooterDefaultLife: Int = 100
  val wallnutDefaultLife: Int = 300
  
  val bullets: Troop => Bullet =
    case p: PeaShooter => PeaBullet(p.pointOfShoot)

  val width: Entity => Int =
    case _: Bullet => 2
    case _: PeaShooter => 5
    case _ => 2

  val fireRates: AttackingAbility => Int =
    case _: PeaShooter => 2
    case _: Zombie => 3
    case _ => 0

  val costs: Plant => Int =
    case _: PeaShooter => 100
    case _: Wallnut => 50
    case _  => 0

  val damages: Bullet => Int =
    case _: PeaBullet => 25
    case _: Paw => 25
    case _ => 0

  val velocity: Entity => Float =
    case _: PeaBullet => 0.06
    case _: Paw => -0.1
    case _ => 0

  val ranges: AttackingAbility => Int =
    case _: PeaShooter => 80
    case _: Zombie => 10
    case _ => 0

