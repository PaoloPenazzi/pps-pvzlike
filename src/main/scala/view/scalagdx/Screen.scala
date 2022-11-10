package view.scalagdx

import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.{Gdx, ScreenAdapter}
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.utils.viewport.Viewport
import Clickable.given
import Utils.*
import scala.language.implicitConversions

/**
 * Can be written to screen.
 */
trait Writable:
  /**
   *
   * @return the string to be written.
   */
  def s: String

  /**
   *
   * @return the position (top left) where the string should be displayed.
   */
  def pos: Vector2

  /**
   *
   * @return the line height.
   */
  def height: Float

/**
 * Contains implementation of [[Writable]]
 */
object Writable:
  /**
   *
   * Basic implementation of [[Writable]]
   */
  case class BasicWritable(s: String, pos: Vector2, height: Float) extends Writable

  /**
   *
   * @see [[Writable]]
   */
  def apply(s: String, pos: Vector2, height: Float): Writable = BasicWritable(s, pos, height)

  /**
   *
   * @see [[Writable]]
   */
  def apply(path: String, x: Float, y: Float, height: Float): Writable =
    BasicWritable(path, Vector2(x, y), height)

  /**
   *
   * @see [[Writable]]
   */
  def apply(s: String, rec: Rectangle): Writable = BasicWritable(s, Vector2(rec.x, rec.y + rec.height), rec.height)

/**
 *
 * Can be drawn to screen.
 */
trait Drawable:
  /**
   *
   * @return file path of the image to be drawn.
   */
  def path: String

  /**
   *
   * @return the bounds where the texture will be drawn into.
   */
  def bounds: Rectangle

/**
 * Contains implementation of [[Drawable]]
 */
object Drawable:

  /**
   *
   * Basic implementation of [[Drawable]]
   */
  case class BasicDrawable(path: String, bounds: Rectangle) extends Drawable

  /**
   *
   * @see [[Drawable]]
   */
  def apply(path: String, bounds: Rectangle): Drawable =
    BasicDrawable(path, bounds)

  /**
   *
   * @see [[Writable]] and [[Rectangle]]
   */
  def apply(path: String, x: Float, y: Float, width: Float, height: Float): Drawable =
    BasicDrawable(path, Rectangle(x, y, width, height))


object Screen:
  /**
   *
   * Define screen behavior in a minimalistic way.
   */
  trait ScreenBehavior:
    /**
     *
     * @return the [[Drawable]]s that should be rendered, called each frame.
     */
    def drawables: Seq[Drawable] = Seq.empty

    /**
     *
     * @return the [[Writable]]s that should be rendered, called each frame.
     */
    def writables: Seq[Writable] = Seq.empty

    /**
     *
     * @return the [[Actor]]s on the screen, called on screen startup.
     */
    def actors: Seq[Actor] = Seq.empty

    /**
     *
     * @return what happens when the user touches the screen in a certain position.
     */
    def onScreenTouch: Vector2 => Unit = _ => {}

    /**
     *
     * @return the [[Viewport]].
     */
    def viewport: Viewport

  /**
   *
   * A basic screen that implements the given behavior.
   * The writables will be rendered in foreground with respect to the drawables.
   * The rendering of both writables and drawables follows the Seq ordering. Last elements of the seq will be in foreground with respect to the first ones.
   *
   * @param behavior the screen behavior
   * @note The drawables are rendered through a memoized approach to reduce workload.
   *       The assumption is that the asset associated with a given filepath will not change at runtime.
   */
  case class BasicScreen(behavior: ScreenBehavior) extends ScreenAdapter:
    private val camera = behavior.viewport.getCamera
    private lazy val stage = new Stage(behavior.viewport)
    private lazy val batch: SpriteBatch = SpriteBatch()
    private lazy val font: BitmapFont = BitmapFont(Gdx.files.internal("assets/gameWindow/font.fnt"))
    font.setUseIntegerPositions(false)

    override def render(delta: Float): Unit =
      Gdx.gl.glClearColor(0, 0, 0, 1)
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
      batch.setProjectionMatrix(camera.combined)
      batch.begin()

      behavior.drawables.foreach(d =>
        batch.draw(memoizedTexture(d.path), d.bounds.x, d.bounds.y, d.bounds.width, d.bounds.height)
      )

      behavior.writables.foreach(w =>
        scaleFont(w.height)
        font.draw(batch, w.s, w.pos.x, w.pos.y)
      )

      batch.end()
      stage.draw()
      stage.act(delta)

    override def show(): Unit =
      stage.clear()
      behavior.actors.foreach(stage.addActor)
      Gdx.input.setInputProcessor(stage)
      stage.onTouchDown(behavior.onScreenTouch)

    override def resize(width: Int, height: Int): Unit =
      behavior.viewport.update(width, height)
      camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0)
      camera.update()

    private def scaleFont(height: Float): Unit =
      font.getData.setScale(height * 2 * font.getScaleY / font.getLineHeight)

    private val memoizedTexture: String => Texture = memoized(texture)
