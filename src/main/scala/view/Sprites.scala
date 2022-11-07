package view

import model.entities.*
import model.entities.WorldSpace.LanesLength
import view.ViewportSpace.gridWidth

object Sprites {
  def spriteName(entity: Entity): String = entity match
    case _: PeaShooter => "peashooter.png"
    case wallnut: Wallnut => 
      wallnut.life match
        case n if n >= PlantDefaultValues.wallnutDefaultLife / 3 * 2 => "wallnut.png"
        case n if n >= PlantDefaultValues.wallnutDefaultLife / 3 => "wallnut_hit.png"
        case _ => "wallnut_crack.png"
    case _: PeaBullet => "peabullet.png"
    case _: PawBullet => "paw.png"
    case _: SwordBullet => "sword.png"
    case _: BasicZombie => "zombie.png"
    case _: FastZombie => "fastzombie.png"
    case _: WarriorZombie => "warriorzombie.png"
    case _: CherryBomb => "cherrybomb.png"
    case _: CherryBullet => "explosion.png"

  def width(entity: Entity): Float = entity.width * (gridWidth/LanesLength)

  def height(entity: Entity): Float = entity match
    case _: PeaBullet => 0.4
    case _: PawBullet => 0.4
    case _: SwordBullet => 0.7
    case _: CherryBullet => 1
    case _: Plant => 1
    case _: Zombie => 1.4
}
