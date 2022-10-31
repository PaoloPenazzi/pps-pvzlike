package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.backends.lwjgl3.*
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout, SpriteBatch}
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, g2d}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Skin}
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{Game, Gdx, ScreenAdapter}
import controller.RootActor
import controller.RootActor.RootCommands.StartGame
import model.entities.Entity
import model.entities.WorldSpace.Position
import view.Game
import view.ViewportSpace.{ViewportHeight, ViewportWidth}


object TestMain:
  @main
  def main(): Unit =
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("Test")
    config.setResizable(false)
    config.setWindowedMode(960, 540)
    config.useVsync(true)
    config.setForegroundFPS(60)
    Lwjgl3Application(TestGame, config)

object TestGame extends Game :
  lazy val batch: SpriteBatch = SpriteBatch()
  lazy val font: BitmapFont = BitmapFont(Gdx.files.internal("assets/gameWindow/font.fnt"), Gdx.files.internal("assets/gameWindow/font.png"), false)
  val viewport: Viewport = FitViewport(ViewportWidth, ViewportHeight)
  val gameScreen: GameScreen = GameScreen()
  val endGameMenu: EndGameMenu = EndGameMenu()

  override def create(): Unit =
    setScreen(endGameMenu)

import view.TestGame

object EndGameMenu:
  val Framerate: Float = 60

  def apply() = new EndGameMenu()

class EndGameMenu() extends ScreenAdapter :
  private lazy val stage = new Stage(TestGame.viewport); //Set up a stage for the ui
  private val camera = TestGame.viewport.getCamera

  override def render(delta: Float): Unit =
    TestGame.batch.setProjectionMatrix(camera.combined)
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    TestGame.batch.begin()
    TestGame.font.getData.setScale(.05f)


    TestGame.batch.end()
    stage.draw(); //Draw the ui
    stage.act(delta)

  override def show(): Unit = ???

  override def resize(width: Int, height: Int): Unit =
    TestGame.viewport.update(width, height)
    camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0)
    camera.update()
