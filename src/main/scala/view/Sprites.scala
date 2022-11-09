package view

import com.badlogic.gdx.graphics.Texture
import model.entities.*
import model.entities.WorldSpace.LanesLength
import view.ViewportSpace.gridWidth

object Sprites {
  val MainMenuBackground: String = "assets/background/mainmenu.png"
  val GameBackground: String = "assets/background/day.png"
  val NewGameButton: String = "assets/new_game_button.png"
  val Sun: String = "assets/gameWindow/sun.png"
  val PauseButton: String = "assets/gameWindow/pause.png"
  val ResumeButton: String = "assets/gameWindow/resume.png"
  def spriteName(entity: Entity): String = "assets/" + (entity match
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
    case _: SwordBullet => "bullets/sword.png")

  def width(entity: Entity): Float = entity.width * (gridWidth/LanesLength)

  def height(entity: Entity): Float = entity match
    case _: PeaBullet => 0.4
    case _: PawBullet => 0.4
    case _: SnowBullet => 0.4
    case _: SwordBullet => 0.7
    case _: CherryBullet => 1
    case _: Plant => 1
    case _: Zombie => 1.4

  def cardName(troop: Troop): String = troop match
    case s: Shooter[_] => s.bulletInstance match
      case _: PeaBullet => "assets/gameWindow/peashooter-card.png"
      case _: SnowBullet => "assets/gameWindow/snowshooter-card.png"
    case _: Wallnut => "assets/gameWindow/wallnut-card.png"
    case _: CherryBomb => "assets/gameWindow/cherrybomb-card.png"
}
