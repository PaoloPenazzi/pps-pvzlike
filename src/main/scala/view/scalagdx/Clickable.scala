package view.scalagdx

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.{Actor, EventListener, InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

/**
 * Given conversions are available to pimp libGDX classes. This enables the usage of:
 *  - [[Clickable.Clickable]] API on [[Actor]] and [[Stage]].
 */
object Clickable:
  /**
   * Add APIs to define reactive behavior that should arise from certain click events.
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
     *          that consumes the pointer coordinates at the time of pressing.
     */
    def onTouchDown(f: Vector2 => Unit): Unit = addListener(new ClickListener :
      override def touchDown(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Boolean =
        super.touchDown(event, x, y, pointerId, buttonId)
        f(Vector2(x, y))
        true
    )

    /**
     * Adds a behavior for when a pointer (previously pressed within the boundaries of this) gets released.
     *
     * @param f the behavior to trigger when the pointer is released.
     */
    def onTouchUp(f: () => Unit): Unit = addListener(new ClickListener :
      override def touchUp(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Unit =
        super.touchUp(event, x, y, pointerId, buttonId)
        f()
    )

  given Conversion[Actor, Clickable] = actor => new Clickable :
    export actor.addListener

  given Conversion[Stage, Clickable] = stage => new Clickable :
    export stage.addListener

