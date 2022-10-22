package model.common

import model.entities.{AttackingAbility, Bullet, Entity, Paw, PeaBullet, PeaShooter, Turret, Zombie}

object DefaultValues:
  val width: Entity => Int =
    case _: Bullet => 2
    case _ => 2

  val fireRates: AttackingAbility => Int =
    case _: PeaShooter => 2
    case _: Zombie => 3
    case _ => 0

  val costs: Turret => Int =
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

