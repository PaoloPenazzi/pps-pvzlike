package view

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import view.Sprites.MainMenuBackground
import view.ScalaGDX.Screen.ScreenBehavior
import view.ScalaGDX.Drawable
import view.ViewportSpace.{ViewportHeight, ViewportWidth}


class MainMenuScreen extends ScreenBehavior:
  override def drawables: Seq[Drawable] =
    Seq(Drawable(MainMenuBackground, 0, 0, ViewportWidth, ViewportHeight))
  override def onScreenTouch: Vector2 => Unit = _ => Game.startNewGame()

  override def viewport: Viewport = Game.viewport
