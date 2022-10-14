package view

import model.entities.*

object Sprites {
  def spriteName(entity: Entity): String = entity match
    case _: Plant => "peashooter.png"
    case _: PeaBullet => "seed.png"
    case _: Zombie => "zombie.png"

  def width(entity: Entity): Float = entity match
    case _: Plant => 1
    case _: PeaBullet => 0.2
    case _: Zombie => 1

  def height(entity: Entity): Float = entity match
    case _: Plant => 1
    case _: PeaBullet => 0.2
    case _: Zombie => 1.5
}
