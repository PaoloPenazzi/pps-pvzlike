package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{Game, Gdx, ScreenAdapter}
import controller.actors.GameLoopActor.GameLoopCommands.Command
import controller.actors.RootActor
import controller.actors.RootActor.RootCommands.{RootCommand, Start}
import model.Statistics.GameStatistics
import view.ScalaGDX.Screen
import view.ScalaGDX.Screen.ScreenBehavior
import view.View.Renderer
import view.ViewportSpace.*


object Game extends com.badlogic.gdx.Game :
  val viewport: Viewport = FitViewport(ViewportWidth, ViewportHeight)
  var actorSystem: Option[ActorSystem[RootCommand]] = None

  def startNewGame(): Unit =
    actorSystem = Some(ActorSystem(RootActor(), "launcher"))
    actorSystem.foreach(_ ! Start())

  def changeScreen(behavior: ScreenBehavior): Unit =
    Gdx.app.postRunnable(new Runnable() :
      override def run(): Unit =
        setScreen(Screen(behavior))

      )

  def endGame(stats: GameStatistics): Unit =
    Gdx.app.postRunnable(new Runnable() :
      override def run(): Unit =
        terminateActorSystem()
        setScreen(Screen(GameOverScreen(stats)))

      )

  private def terminateActorSystem(): Unit =
    actorSystem.foreach(_.terminate())

  override def create(): Unit =
    setScreen(Screen(MainMenuScreen()))

  override def dispose(): Unit =
    super.dispose()
    terminateActorSystem()
