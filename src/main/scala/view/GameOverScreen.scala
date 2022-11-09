package view

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.viewport.Viewport
import model.Statistics.GameStatistics
import view.ScalaGDX.{ImageButtons, Writable, given}
import view.ScalaGDX.Screen.ScreenBehavior
import view.ScalaGDX.Utils.texture
import view.ScalaGDX.ActorBehaviors.*
import view.Sprites.{MainMenuBackground, NewGameButton}
import view.ViewportSpace.{HUDHeight, ViewportHeight, ViewportWidth}

import scala.language.implicitConversions

class GameOverScreen(stats: GameStatistics) extends ScreenBehavior:
  override def writables: Seq[Writable] =
    Seq(
      Writable("Game Over", 0.4, 8.5, 1.4),
      Writable("Placed plants: " + stats.getPlants.size, ViewportWidth / 4, 6, 0.5),
      Writable("Killed zombies: " + stats.getZombies.size, ViewportWidth / 4, 5, 0.5),
      Writable("Reached wave: " + stats.rounds, ViewportWidth / 4, 4, 0.5)
    )
  override def actors: Seq[Actor] =
    val fadeIn = FadeWidget(true, 1)
    val fadeOut = FadeWidget(false, 1)
    val width = ViewportWidth / 2
    val button: Actor = ImageButtons.builder withTexture texture(NewGameButton) withBounds(ViewportWidth / 2 - width / 2, 0.2, width, HUDHeight * 2)
    button.addPulseOnTouch()
    button.onTouchUp(() =>
      fadeOut.play(() =>
        Game.startNewGame()))
    Seq(button, fadeIn, fadeOut)

  override def viewport: Viewport = Game.viewport

