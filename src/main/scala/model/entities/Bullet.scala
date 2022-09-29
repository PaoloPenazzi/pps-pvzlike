package model.entities

import model.common.DefaultValues.*

trait Bullet extends MovingEntity with Entity:
  def damage: Int = damages(this)

  def updatePositionAfter(timeElapsed: Double): Unit =
    position = (position._1 + (timeElapsed * velocity), position._2)

  def checkCollisionAgainst(enemy: Enemy): Boolean =
    position._1 == enemy.position._1

class Seed() extends Bullet:
  override def velocity: Double = 5.0