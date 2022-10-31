package view

import model.common.DefaultValues
import model.entities.*

object Sprites {
  def spriteName(entity: Entity): String = entity match
    case _: PeaShooter => "peashooter.png"
    case wallnut: Wallnut => 
      wallnut.life match
        case n if n >= DefaultValues.wallnutDefaultLife / 3 * 2 => "wallnut.png"
        case n if n >= DefaultValues.wallnutDefaultLife / 3 => "wallnut_hit.png"
        case _ => "wallnut_crack.png"
    case _: PeaBullet => "peabullet.png"
    case _: PawBullet => "paw.png"
    case _: Zombie => "zombie.png"
    case _: FastZombie => "fastzombie.png"

  def width(entity: Entity): Float = entity match
    case _: PeaShooter => 1
    case _: Wallnut => 1
    case _: PeaBullet => 0.4
    case _: PawBullet => 0.4
    case _: Zombie => 1

  def height(entity: Entity): Float = entity match
    case _: PeaShooter => 1
    case _: Wallnut => 1
    case _: PeaBullet => 0.4
    case _: PawBullet => 0.4
    case _: Zombie => 1.5
}
