package model.common

import model.entities.*
import scala.language.implicitConversions

object DefaultValues:
  val bullets: Troop => Bullet =
    case p: PeaShooter => PeaBullet(p.position)

  val width: Entity => Int =
    case _: Bullet => 2
    case _ => 2

  val defaultPlantState: TroopState = TroopState.Idle
  val peashooterDefaultLife: Int = 100
  val wallnutDefaultLife: Int = 300

  val fireRates: AttackingAbility => Int =
    case _: PeaShooter => 2
    case _: Zombie => 3
    case _ => 0

  val costs: Plant => Int =
    case _: PeaShooter => 100
    case _  => 0

  val damages: Bullet => Int =
    case _: PeaBullet => 25
    case _: Paw => 25
    case _ => 0

  val ranges: AttackingAbility => Int =
    case _: PeaShooter => 500
    case _: Zombie => 10
    case _ => 0

