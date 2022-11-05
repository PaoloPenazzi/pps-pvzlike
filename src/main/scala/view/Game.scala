package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{Game, Gdx, ScreenAdapter}
import controller.RootActor
import controller.RootActor.RootCommands.{RootCommand, Start}
import controller.GameLoopActor.GameLoopCommands.Command
import ViewportSpace.*

object Game extends com.badlogic.gdx.Game:
  val viewport: Viewport = FitViewport(ViewportWidth.toFloat, ViewportHeight.toFloat)
  var mainMenuScreen: ScreenAdapter = MainMenuScreen()
  var actorSystem: Option[ActorSystem[RootCommand]] = None
  lazy val batch: SpriteBatch = SpriteBatch()
  lazy val font: BitmapFont = BitmapFont(Gdx.files.internal("assets/gameWindow/font.fnt"), Gdx.files.internal("assets/gameWindow/font.png"), false)

  def startNewGame(): Unit =
    Gdx.app.postRunnable(new Runnable():
      override def run(): Unit =
        val gameScreen = GameScreen()
        setScreen(gameScreen)
        actorSystem = Some(ActorSystem(RootActor(), "launcher"))
        actorSystem.foreach(_ ! Start(gameScreen))
    )

  def endGame(): Unit =
    Gdx.app.postRunnable(new Runnable():
      override def run(): Unit =
        actorSystem.foreach(_.terminate())
        setScreen(EndGameMenu())
      )

  override def create(): Unit =
    setScreen(mainMenuScreen)
