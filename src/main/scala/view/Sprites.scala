package view

import model.entities.*

object Sprites {
  def spriteName(entity: Entity): String = entity match
    case _: Plant => "peashooter.png"
    case _: Seed => "seed.png"
    case _: Zombie => "zombie.png"
}
