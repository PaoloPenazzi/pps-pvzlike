trait Enemy extends Entity with MovingEntity with AttackingEntity:
  override def velocity: Double = 1.0


class Zombie() extends Enemy:
  override def boundary: Boundary = super.boundary
  override def direction: String = "left"