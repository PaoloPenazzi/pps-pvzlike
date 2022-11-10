package view

import com.badlogic.gdx.graphics.Texture
import model.entities.*
import model.entities.WorldSpace.LanesLength

/**
 * Configuration of assets filepath.
 */
object Sprites {
  val MainMenuBackground: String = "assets/background/mainmenu.png"
  val GameBackground: String = "assets/background/day.png"
  val NewGameButton: String = "assets/new_game_button.png"
  val Sun: String = "assets/gameWindow/sun.png"
  val PauseButton: String = "assets/gameWindow/pause.png"
  val ResumeButton: String = "assets/gameWindow/resume.png"
  val FastButton: String = "assets/gameWindow/fast.png"
  val NormalButton: String = "assets/gameWindow/normal.png"

  /**
   * @return the filepath of the asset corresponding to given entity.
   */
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

  /**
   * @return the filepath of the asset corresponding to the card button to place given Troop
   */
  def cardName(troop: Troop): String = troop match
    case s: Shooter[_] => s.bulletInstance match
      case _: PeaBullet => "assets/gameWindow/peashooter-card.png"
      case _: SnowBullet => "assets/gameWindow/snowshooter-card.png"
    case _: Wallnut => "assets/gameWindow/wallnut-card.png"
    case _: CherryBomb => "assets/gameWindow/cherrybomb-card.png"
}
