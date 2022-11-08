package view

import com.badlogic.gdx.{Gdx, ScreenAdapter}
import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.scenes.scene2d.{Actor, EventListener, InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.Viewport

import scala.language.implicitConversions

/**
 * A layer on top of libGDX, designed to facilitate its use by providing a more fluent API
 * and reducing the need for boilerplate code.
 *
 * Given conversions are available to pimp libGDX classes. This enables the usage of:
 *  - [[view.ScalaGDX.Clickable]] API on [[Actor]] and [[Stage]].
 */ 
object ScalaGDX:
  /**
   * Add simpler APIs to define reactive behavior that should arise from certain click events.
   */
  trait Clickable:
    /**
     * See [[com.badlogic.gdx.scenes.scene2d.Actor.addListener]]
     */
    protected def addListener(cl: EventListener): Boolean

    /**
     * Registers a behavior for when a pointer is pressed within the boundaries of this.
     *
     * @param f the behavior to trigger when the pointer is pressed,
     * that consumes the pointer coordinates at the time of pressing.
     */
    def onTouchDown(f: Vector2 => Unit): Unit = addListener(new ClickListener:
      override def touchDown(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Boolean =
        super.touchDown(event, x, y, pointerId, buttonId)
        f(Vector2(x,y))
        true
    )

    /**
     * Adds a behavior for when a pointer (previously pressed within the boundaries of this) gets released.
     *
     * @param f the behavior to trigger when the pointer is released.
     */
    def onTouchUp(f: () => Unit): Unit = addListener(new ClickListener:
      override def touchUp(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Unit =
        super.touchUp(event, x, y, pointerId, buttonId)
        f()
    )

  given Conversion[Actor, Clickable] = actor => new Clickable:
    export actor.addListener

  given Conversion[Stage, Clickable] = stage => new Clickable:
    export stage.addListener


  /**
   * Memoize a function from A to B using a map.
   *
   * @param f a function that computes a value B from a key A
   * @tparam A the key type
   * @tparam B the value type
   * @return a memoized version of f
   */
  def memoized[A, B](f: A => B): A => B =
      val cache = collection.mutable.Map.empty[A, B]

      a =>
        cache.getOrElse(a, {
          cache.update(a, f(a))
          cache(a)
        })

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
     * @return the scale of this writing.
     */
    def scale: Float

  /**
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

  object Screen:
    /**
     * Define screen behavior in a minimalistic way.
     */
    trait ScreenBehavior:
      /**
       *
       * @return the [[Drawable]]s that should be rendered each frame.
       */
      def drawables: Seq[Drawable]

      /**
       *
       * @return the [[Writable]]s that should be rendered each frame.
       */
      def writables: Seq[Writable]

      /**
       *
       * @return the [[Actor]]s on the screen.
       */
      def actors: Seq[Actor]

      /**
       *
       * @return what happens when the user touches the screen in a certain position.
       */
      def onScreenTouch: Vector2 => Unit

      /**
       *
       * @return the [[Viewport]].
       */
      def viewport: Viewport

    /**
     *
     * @param behavior the screen logic
     * @return a simple screen that implements the screen logic.
     * @note The drawables are rendered through a memoized approach.
     *       The assumption is that the asset associated with the filepath will not change at runtime.
     */
    def apply(behavior: ScreenBehavior): ScreenAdapter = new ScreenAdapter :
      private val camera = behavior.viewport.getCamera
      private lazy val stage = new Stage(behavior.viewport)
      private lazy val batch: SpriteBatch = SpriteBatch()
      private lazy val font: BitmapFont = BitmapFont(Gdx.files.internal("assets/gameWindow/font.fnt"), Gdx.files.internal("assets/gameWindow/font.png"), false)

      override def render(delta: Float): Unit =
        Gdx.gl.glClearColor(0, 0, 0, 1)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.setProjectionMatrix(camera.combined)

        batch.begin()
        behavior.writables.foreach(w =>
          font.getData.setScale(w.scale)
          font.draw(batch, w.s, w.pos.x, w.pos.y)
        )
        behavior.drawables.foreach(d =>
          batch.draw(texture(d.path), d.bounds.x, d.bounds.y, d.bounds.width, d.bounds.height)
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

      private val texture: String => Texture = memoized(path => new Texture(Gdx.files.classpath(path)))
