package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.graphics.g2d.{BitmapFont, Sprite, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{ApplicationAdapter, Game, Gdx}
import controller.RootActor
import controller.RootActor.RootCommands.StartGame
import ViewportSpace.*

import scala.language.implicitConversions


object Game extends com.badlogic.gdx.Game:
  val viewport: Viewport = FitViewport(ViewportWidth,ViewportHeight)
  val gameScreen: GameScreen = GameScreen()
  val mainMenuScreen: MainMenuScreen = MainMenuScreen(gameScreen)
  lazy val batch: SpriteBatch = SpriteBatch()
  lazy val font: BitmapFont = BitmapFont(Gdx.files.internal("assets/gameWindow/font.fnt"), Gdx.files.internal("assets/gameWindow/font.png"), false)
  override def create(): Unit =
    setScreen(mainMenuScreen)
    
   

