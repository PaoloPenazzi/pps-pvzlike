package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{Game, Gdx, ScreenAdapter}
import controller.actors.GameLoopActor.GameLoopCommands.Command
import controller.actors.RootActor
import controller.actors.RootActor.RootCommands.{RootCommand, Start}
import model.Statistics.GameStatistics
import scalagdx.Screen.ScreenBehavior
import scalagdx.Screen.BasicScreen.given
import ViewportSpace.{ViewportWidth, ViewportHeight}
import scala.language.implicitConversions


/**
 * Handles view screen change and game startup / shut down.
 * Acts as the LWJGL3 entry point of the application.
 */
object Game extends com.badlogic.gdx.Game :
  val viewport: Viewport = FitViewport(ViewportWidth, ViewportHeight)
  private var actorSystem: Option[ActorSystem[RootCommand]] = None

  /**
   * Spawns the game actor system.
   */
  def startNewGame(): Unit =
    actorSystem = Some(ActorSystem(RootActor(), "launcher"))
    actorSystem.foreach(_ ! Start())

  /**
   * Change screen to one using the given behavior.
   */
  def changeScreen(behavior: ScreenBehavior): Unit =
    Gdx.app.postRunnable(() =>
        setScreen(behavior)
    )

  /**
   * Shuts down the game actor system and change screen to a post-game recap with given [[GameStatistics]].
   */
  def endGame(stats: GameStatistics): Unit =
    Gdx.app.postRunnable(() =>
        terminateActorSystem()
        changeScreen(GameOverScreen(stats))
    )

  private def terminateActorSystem(): Unit =
    actorSystem.foreach(_.terminate())

  override def create(): Unit =
    changeScreen(MainMenuScreen())

  override def dispose(): Unit =
    super.dispose()
    terminateActorSystem()
