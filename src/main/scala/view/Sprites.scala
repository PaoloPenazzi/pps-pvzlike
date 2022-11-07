package view

import model.entities.*
import model.entities.WorldSpace.LanesLength
import view.ViewportSpace.gridWidth

object Sprites {
  def spriteName(entity: Entity): String = entity match
    case s: Shooter[_] => s.bullet match
      case _: PeaBullet => "troops/peashooter.png"
      case _: SnowBullet => "troops/snowshooter.png"
      case _ => ""
    case wallnut: Wallnut => 
      wallnut.life match
        case n if n >= PlantDefaultValues.wallnutDefaultLife / 3 * 2 => "troops/wallnut.png"
        case n if n >= PlantDefaultValues.wallnutDefaultLife / 3 => "troops/wallnut_hit.png"
        case _ => "troops/wallnut_crack.png"
    case _: CherryBomb => "troops/cherrybomb.png"
    case _: BasicZombie => "troops/zombie.png"
    case _: FastZombie => "troops/fastzombie.png"
    case _: WarriorZombie => "troops/warriorzombie.png"
    case _: PeaBullet => "bullets/peabullet.png"
    case _: SnowBullet => "bullets/snowbullet.png"
    case _: CherryBullet => "bullets/explosion.png"
    case _: PawBullet => "bullets/paw.png"
    case _: SwordBullet => "bullets/sword.png"

  def width(entity: Entity): Float = entity.width * (gridWidth/LanesLength)

  def height(entity: Entity): Float = entity match
    case _: PeaBullet => 0.4
    case _: PawBullet => 0.4
    case _: SnowBullet => 0.4
    case _: SwordBullet => 0.7
    case _: CherryBullet => 1
    case _: Plant => 1
    case _: Zombie => 1.4
}
