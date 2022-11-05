package view

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Skin, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.{Gdx, ScreenAdapter}
import model.entities.Entity
import model.entities.WorldSpace.Position
import view.Game
import view.ViewportSpace.*

object EndGameMenu:
  def apply() = new EndGameMenu()

  private def createButton(text: String, x: Float, y: Float)(func: => Boolean): Button =
    val button = new TextButton(text, new Skin(Gdx.files.internal("assets/skin/default/uiskin.json")))
    button.setTransform(true)
    button.setScale(.05f)
    button.setPosition(x, y)
    button.addListener(new ClickListener {
      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = func
    })
    button

  class EndGameMenu() extends ScreenAdapter :
    private lazy val stage = new Stage(Game.viewport); //Set up a stage for the ui

    override def render(delta: Float): Unit =
      Gdx.gl.glClearColor(0, 0, 0, 1)
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

      Game.batch.begin()
      Game.font.getData.setScale(0.05f)
      Game.font.draw(Game.batch, "PVZ", ViewportWidth.toFloat / 2.3f, ViewportHeight - HUDHeight / 2)
      Game.batch.end()
      stage.draw(); //Draw the ui
      stage.act(delta)

    override def show(): Unit =
      val restart = createButton("Restart", ViewportWidth.toFloat / 2.5f, ViewportHeight - HUDHeight / 0.25f) {
        Game.startNewGame()
        dispose()
        false
      }
      stage.addActor(restart)
      Gdx.input.setInputProcessor(stage)

