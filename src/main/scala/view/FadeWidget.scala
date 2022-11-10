package view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.utils.viewport.Viewport
import ViewportSpace.*
import scalagdx.Utils.{texture => textureOfColor}

/**
 * A widget for fade in / fade out animation on viewport.
 *
 * @param in true to make this a fadeIn widget, false to make this a fadeOut widget
 * @param duration the duration of the fade in/out animation
 *
 * @note if the "in" parameter is set to true, the fade in animation will play immediately
 */
class FadeWidget(in: Boolean, val duration: Float) extends Widget:
  private var texture: Option[Texture] = None
  if in then
    play()
  else
    addAction(Actions.alpha(0))

  /**
   * Play the animation.
   *
   * @param callback the runnable to be executed after the animation is over
   */
  def play(callback: Runnable = () => {}): Unit =
    val sequence = SequenceAction()
    if in then
      sequence.addAction(Actions.alpha(1))
      sequence.addAction(Actions.alpha(0, duration, Interpolation.pow2Out))
    else
      sequence.addAction(Actions.alpha(0))
      sequence.addAction(Actions.alpha(1, duration, Interpolation.pow2In))
    sequence.addAction(Actions.run(callback))
    addAction(sequence)

  override def setStage(stage: Stage): Unit =
    super.setStage(stage)
    if stage != null then
      texture = Some(textureOfColor(Color.BLACK))

  override def draw(batch: Batch, parentAlpha: Float): Unit =
    super.draw(batch, parentAlpha)
    validate()
    batch.setColor(getColor)
    batch.draw(texture.get, 0, 0, ViewportWidth, ViewportHeight)
