package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.graphics.g2d.{BitmapFont, TextureRegion}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, TextureRegionDrawable}
import controller.RootActor
import controller.RootActor.RootCommands.StartGame
import view.ViewportSpace.{HUDHeight, ViewportHeight, ViewportWidth}

object MainMenuScreen:
  def apply() = new MainMenuScreen()

  class MainMenuScreen() extends ScreenAdapter:
    private lazy val stage = new Stage(Game.viewport); //Set up a stage for the ui

    override def render(delta: Float): Unit =
      //Game.batch.setProjectionMatrix(camera.combined)
      Gdx.gl.glClearColor(0, 0, 0, 1)
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
      Game.batch.begin()
      Game.font.getData.setScale(0.05f)
      Game.font.draw(Game.batch, "Ma che ogoogogogoggo", ViewportWidth.toFloat / 2.5f, ViewportHeight - HUDHeight / 0.25f)
      Game.batch.end()
      stage.draw(); //Draw the ui
      stage.act(delta)


    override def show(): Unit =
      //stage.clear()
      val startGameButton: Texture = new Texture(Gdx.files.classpath("assets/startGameButton.png"))
      createButtonFromImage(startGameButton, ViewportWidth / 2 - 1, ViewportHeight - HUDHeight, 1.5f, 1.2f)
      Gdx.input.setInputProcessor(stage)

    override def resize(width: Int, height: Int): Unit =
      Game.viewport.update(width, height)



    private def createButtonFromImage(texture: Texture, x: Float, y: Float, width: Float, height: Float): Unit =
      val textureRegionDrawable = new TextureRegion(texture)
      val button = new ImageButton(new TextureRegionDrawable(textureRegionDrawable))
      button.setBounds(x, y, width, height)
      button.setTransform(true)
      button.addListener(new ClickListener {
        override def touchDown(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Boolean =
          super.touchDown(event, x, y, pointerId, buttonId)
          if buttonId == Buttons.LEFT then
            button.clearActions()
            button.setScale(1.2f)
            true
          else false
        override def touchUp(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Unit =
          super.touchUp(event, x, y, pointerId, buttonId)
          button.addAction(Actions.scaleTo(1f,1f, 0.5f))
          Game.startNewGame()
          dispose()
      })
      stage.addActor(button)