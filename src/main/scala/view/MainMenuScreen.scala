package view

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.viewport.Viewport
import Sprites.MainMenuBackground
import scalagdx.Screen.ScreenBehavior
import scalagdx.Drawable
import ViewportSpace.{ViewportHeight, ViewportWidth}
import scalagdx.FadeWidget


class MainMenuScreen extends ScreenBehavior:
  var fadeOut: Option[FadeWidget] = None

  override def drawables: Seq[Drawable] =
    Seq(Drawable(MainMenuBackground, 0, 0, ViewportWidth, ViewportHeight))

  override def actors: Seq[Actor] =
    val fadeIn = FadeWidget(true, 1)
    fadeOut = Some(FadeWidget(false, 1))
    Seq(fadeIn, fadeOut.get)

  override def onScreenTouch: Vector2 => Unit = _ =>
    fadeOut.get.play(() => Game.startNewGame())

  override def viewport: Viewport = Game.viewport
