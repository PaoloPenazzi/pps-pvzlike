package model.common

import model.entities.{AttackingEntity, Bullet, Entity, Seed, Zombie, Plant, Turret}

object DefaultValues:
  val width: Entity => Int =
    case _: Bullet => 5
    case _ => 20

  val HP: AttackingEntity => Int =
    case _: Plant => 300
    case _: Zombie => 100
    case _ => 0

  val fireRates: AttackingEntity => Int =
    case _: Plant => 2
    case _: Zombie => 3
    case _ => 0

  val costs: Turret => Int =
    case _: Plant => 100
    case _  => 0

  val damages: Bullet => Int =
    case _: Seed => 25
    case _       => 0

  val ranges: AttackingEntity => Int =
    case _: Plant => 500
    case _: Zombie => 10
    case _ => 0

