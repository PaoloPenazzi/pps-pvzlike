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

object Game extends com.badlogic.gdx.Game:
  val viewport: Viewport = FitViewport(ViewportWidth, ViewportHeight)
  var actorSystem: Option[ActorSystem[RootCommand]] = None

  def startNewGame(): Unit =
    Gdx.app.postRunnable(new Runnable():
      override def run(): Unit =
        val gameScreen = GameScreen()
        setScreen(Screen(gameScreen))
        actorSystem = Some(ActorSystem(RootActor(), "launcher"))
        actorSystem.foreach(_ ! Start(gameScreen))
    )

  def endGame(stats: GameStatistics): Unit =
    Gdx.app.postRunnable(new Runnable():
      override def run(): Unit =
        actorSystem.foreach(_.terminate())
        setScreen(EndGameMenu(stats))
      )

  override def create(): Unit =
    setScreen(Screen(MainMenuScreen()))
