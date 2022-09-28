package model.entities

trait Enemy extends MovingEntity with AttackingEntity:
  override def velocity: Double = 1.0

class Zombie() extends Enemy