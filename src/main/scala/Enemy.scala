trait Enemy extends Entity with MovingEntity with AttackingEntity:
  override def velocity: Double = 1.0


class Zombie(override val position: (Int, Int)) extends Enemy:
  override def boundary: Boundary = super.boundary
