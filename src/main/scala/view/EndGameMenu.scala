package view

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Skin, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.{Gdx, ScreenAdapter}
import controller.RootActor.RootCommands.StartGame
import model.entities.Entity
import model.entities.WorldSpace.Position
import view.Game
import view.ViewportSpace.*
import ScalaGDX.*
import ScalaGDX.given
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}

import scala.language.implicitConversions

object EndGameMenu:
  def apply() = new EndGameMenu()

  class EndGameMenu() extends ScreenAdapter :
    private lazy val stage = new Stage(Game.viewport)
    lazy val font: BitmapFont = BitmapFont(Gdx.files.internal("assets/gameWindow/font.fnt"), Gdx.files.internal("assets/gameWindow/font.png"), false)
    lazy val batch: SpriteBatch = SpriteBatch()
    override def render(delta: Float): Unit =
      Gdx.gl.glClearColor(0, 0, 0, 1)
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

      batch.begin()
      font.getData.setScale(0.05f)
      font.draw(batch, "PVZ", ViewportWidth.toFloat / 2.3f, ViewportHeight - HUDHeight / 2)
      batch.end()
      stage.draw(); //Draw the ui
      stage.act(delta)

    override def show(): Unit =
      val restart = createButton("Restart", ViewportWidth.toFloat / 2.5f, ViewportHeight - HUDHeight / 0.25f) {
        Game.startNewGame()
        dispose()
      }
      stage.addActor(restart)
      Gdx.input.setInputProcessor(stage)
  
  private def createButton(text: String, x: Float, y: Float)(f: => Unit): Button =
    val button = new TextButton(text, new Skin(Gdx.files.internal("assets/skin/default/uiskin.json")))
    button.setTransform(true)
    button.setScale(.05f)
    button.setPosition(x, y)
    button.onTouchDown(_ => f)
    button

