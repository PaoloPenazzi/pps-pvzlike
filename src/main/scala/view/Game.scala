package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{Game, Gdx, ScreenAdapter}
import controller.RootActor
import controller.RootActor.RootCommands.{RootCommand, Start}
import controller.GameLoopActor.GameLoopCommands.Command
import ViewportSpace.*
import model.Statistics.GameStatistics
import ScalaGDX.Screen
import view.ScalaGDX.Screen.ScreenBehavior
import view.View.Renderer


object Game extends com.badlogic.gdx.Game:
  val viewport: Viewport = FitViewport(ViewportWidth, ViewportHeight)
  var actorSystem: Option[ActorSystem[RootCommand]] = None

  def startNewGame(): Unit =
    actorSystem = Some(ActorSystem(RootActor(), "launcher"))
    actorSystem.foreach(_ ! Start())

  def changeScreen(behavior: ScreenBehavior): Unit =
    Gdx.app.postRunnable(new Runnable():
      override def run(): Unit =
        setScreen(Screen(behavior)))

  def endGame(stats: GameStatistics): Unit =
    Gdx.app.postRunnable(new Runnable():
      override def run(): Unit =
        terminateActorSystem()
        setScreen(Screen(GameOverScreen(stats)))
      )

  override def create(): Unit =
    setScreen(Screen(MainMenuScreen()))

  override def dispose(): Unit =
    super.dispose()
    terminateActorSystem()

  private def terminateActorSystem(): Unit =
    actorSystem.foreach(_.terminate())
