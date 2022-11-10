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
  case class ImageButtonBuilder private[ImageButtons](texture: Option[Texture] = None, style: Option[ImageButtonStyle] = None, bounds: Rectangle = Rectangle(0, 0, 0, 0)):
    /**
     * Define the texture the button should use
     */
    def withTexture(t: Texture): ImageButtonBuilder = copy(texture = Some(t))

    /**
     * Define the style the button should use
     */
    def withStyle(s: ImageButtonStyle): ImageButtonBuilder = copy(style = Some(s))

    /**
     * Define bounds for the button
     */
    def withBounds(x: Float, y: Float, width: Float, height: Float): ImageButtonBuilder = copy(bounds = Rectangle(x, y, width, height))

    /**
     * Build the image button. You need to provide a texture or a style before calling this.
     *
     * @throws NoSuchElementException if this is called without first providing a texture or a style
     * @return the image button
     */
    def build: ImageButton =
      val button =
        if style.isDefined
        then ImageButton(style.get)
        else ImageButton(TextureRegionDrawable(texture.get))
      button.setBounds(bounds.x, bounds.y, bounds.width, bounds.height)
      button.setTransform(true)
      button

  /**
   * @return an [[ImageButtonBuilder]].
   */
  def builder: ImageButtonBuilder = ImageButtonBuilder()

  given Conversion[ImageButtonBuilder, ImageButton] = _.build


