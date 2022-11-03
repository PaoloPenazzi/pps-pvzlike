package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import controller.RootActor
import controller.RootActor.RootCommands.StartGame

object MainMenuScreen:
  def apply() = new MainMenuScreen()

  class MainMenuScreen() extends ScreenAdapter:
    private lazy val camera: OrthographicCamera = OrthographicCamera()

    override def render(delta: Float): Unit =
      ScreenUtils.clear(0, 0, 0.2f, 1)
      Gdx.gl.glClearColor(0, 0, 0, 1)
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)ì
      camera.update()
      Game.batch.setProjectionMatrix(camera.combined)
      Game.batch.begin()
      Game.font.draw(Game.batch, "Welcome to Plant vs Zombie!!! ", 100, 150)
      Game.font.draw(Game.batch, "Tap anywhere to begin!", 100, 100)
      Game.batch.end()
      if Gdx.input.isTouched() then
        Game.startNewGame()
        dispose()

    override def show(): Unit =
      camera.setToOrtho(false, 800, 480)
