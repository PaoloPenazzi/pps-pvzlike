package model.entities

import model.common.DefaultValues.*
import model.entities.Turrets.*
import model.entities.{AttackingEntity, Boundary, Bullet, Enemy, Entity, Seed, StationaryEntity, Zombie}

object Turrets:
  trait Turret extends Entity with AttackingEntity with StationaryEntity:
    def cost: Int = costs(this)

    def canAttack(enemy: Enemy): Boolean =
      isOnMyPath(enemy) && isInRange(enemy)

    private def isOnMyPath(enemy: Enemy): Boolean =
      enemy.position._2 == position._2

    private def isInRange(enemy: Enemy): Boolean =
      enemy.position._1 <= range


  /**
   * Basic turret.
   *
   * @param position The position in which the plant is placed by the player.
   */
  class Plant(override val position: (Int, Int)) extends Turret
