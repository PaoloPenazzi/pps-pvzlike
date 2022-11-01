package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.backends.lwjgl3.*
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout, SpriteBatch, TextureAtlas}
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, g2d}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.{Button, Label, Skin, TextButton}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{EventListener, InputEvent, Stage}
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{Gdx, ScreenAdapter}
import controller.RootActor
import controller.RootActor.RootCommands.StartGame
import model.entities.Entity
import model.entities.WorldSpace.Position
import view.Game
import view.ViewportSpace.*

object EndGameMenu:
  def apply() = new EndGameMenu()

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
  
  private def createButton(text: String, x: Float, y: Float)(func: => Boolean): Button =
    val button = new TextButton(text, new Skin(Gdx.files.internal("assets/skin/default/uiskin.json")))
    button.setTransform(true)
    button.setScale(.05f)
    button.setPosition(x, y)
    button.addListener(new ClickListener {
      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = func
    })
    button

