package model.common

import model.entities.Turrets.{Plant, Turret}
import model.entities.{AttackingEntity, Boundary, Bullet, Entity, Seed, Turrets, Zombie}

object DefaultValues:
  val boundaries: Entity => Boundary =
    case _: Bullet => Boundary(5, 5)
    case _ => Boundary(20, 40)

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
    case _        => 0

  val damages: Bullet => Int =
    case _: Seed => 25
    case _       => 0

  val ranges: AttackingEntity => Int =
    case _: Plant => 500
    case _: Zombie => 10
    case _ => 0

