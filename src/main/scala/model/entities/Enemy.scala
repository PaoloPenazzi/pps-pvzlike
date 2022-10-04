package model.entities

import model.entities.WorldSpace.Position

trait Enemy extends MovingEntity with AttackingEntity:
  override def velocity: Float = 1.0

class Zombie() extends Enemy