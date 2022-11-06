package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import view.ViewportSpace.{HUDHeight, ViewportHeight, ViewportWidth}
import ScalaGDX.*
import ScalaGDX.given

import scala.language.implicitConversions

object MainMenuScreen:
  def apply() = new MainMenuScreen()

  class MainMenuScreen() extends ScreenAdapter:
    private val camera = Game.viewport.getCamera
    private lazy val stage = new Stage(Game.viewport)
    private lazy val background: Texture = Texture(Gdx.files.classpath("assets/background/mainmenu.png"))

    override def render(delta: Float): Unit =
      Game.batch.setProjectionMatrix(camera.combined)
      Gdx.gl.glClearColor(0, 0, 0, 1)
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
      Game.batch.begin()
      Game.batch.draw(background, 0, 0, ViewportWidth, ViewportHeight)
      Game.batch.end()
      stage.draw()
      stage.act(delta)

    override def show(): Unit =
      stage.clear()
      Gdx.input.setInputProcessor(stage)
      stage.onTouchDown(_ =>
        dispose()
        Game.startNewGame()
      )

    override def resize(width: Int, height: Int): Unit =
      Game.viewport.update(width, height)
      camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0)
      camera.update()
