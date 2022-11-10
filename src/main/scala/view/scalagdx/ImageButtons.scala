package view.scalagdx

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

/**
 * Utils related to [[ImageButton]]s.
 * A given conversion is available to make usage of [[view.ScalaGDX.ImageButtons.ImageButtonBuilder]] more fluent, eliminating the need to call build method.
 */
object ImageButtons:
  /**
   * A builder for [[ImageButton]]s.
   */
  case class ImageButtonBuilder private[ImageButtons](source: Texture|ImageButtonStyle, bounds: Rectangle = Rectangle(0, 0, 0, 0)):
    /**
     * Define bounds for the button
     */
    def withBounds(x: Float, y: Float, width: Float, height: Float): ImageButtonBuilder = copy(bounds = Rectangle(x, y, width, height))

    /**
     * Build the image button.
     * @return the image button
     */
    def build: ImageButton =
      val button = source match
        case t: Texture => ImageButton(TextureRegionDrawable(t))
        case s: ImageButtonStyle => ImageButton(s)
      button.setBounds(bounds.x, bounds.y, bounds.width, bounds.height)
      button.setTransform(true)
      button

  /**
   * @return an [[ImageButtonBuilder]] using the given source.
   */
  def withSource(source: Texture|ImageButtonStyle): ImageButtonBuilder = ImageButtonBuilder(source)

  given Conversion[ImageButtonBuilder, ImageButton] = _.build


