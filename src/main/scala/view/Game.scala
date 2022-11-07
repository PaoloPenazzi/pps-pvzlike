package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{Game, Gdx, ScreenAdapter}
import controller.RootActor
import controller.RootActor.RootCommands.StartGame
import ViewportSpace.*
import controller.Command

object Game extends com.badlogic.gdx.Game:
  val viewport: Viewport = FitViewport(ViewportWidth, ViewportHeight)
  var actorSystem: Option[ActorSystem[Command]] = None

  def startNewGame(): Unit =
    Gdx.app.postRunnable(new Runnable():
      override def run(): Unit =
        val gameScreen = GameScreen()
        setScreen(gameScreen)
        actorSystem = Some(ActorSystem(RootActor(), "launcher"))
        actorSystem.foreach(_ ! StartGame(gameScreen))
    )

  def endGame(): Unit =
    Gdx.app.postRunnable(new Runnable():
      override def run(): Unit =
        actorSystem.foreach(_.terminate())
        setScreen(EndGameMenu())
      )

  override def create(): Unit =
    setScreen(MainMenuScreen())
