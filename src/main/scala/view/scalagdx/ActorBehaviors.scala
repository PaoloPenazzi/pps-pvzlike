package view.scalagdx

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import Clickable.given
import scala.language.implicitConversions

/**
 * Contains extension methods for actors, adding various types of behavior
 */
object ActorBehaviors:
  extension (a: Actor)

    /**
     * Add the following behavior to this:
     *  - immediate scale up on touch down
     *  - a subsequent transition back to normal after touch up
     */
    def addPulseOnTouch(): Actor =
      a.onTouchDown(_ =>
        a.clearActions()
        a.setScale(1.05f)
      )
      a.onTouchUp(() => a.addAction(Actions.scaleTo(1f, 1f, 0.5f)))
      a