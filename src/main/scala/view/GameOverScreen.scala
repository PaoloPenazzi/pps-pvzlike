package view

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.viewport.Viewport
import model.Statistics.GameStatistics
import view.ScalaGDX.{PulsingImageButton, Writable, given}
import view.ScalaGDX.Screen.ScreenBehavior
import view.ScalaGDX.Utils.texture
import view.Sprites.{MainMenuBackground, NewGameButton}
import view.ViewportSpace.{HUDHeight, ViewportHeight, ViewportWidth}

import scala.language.implicitConversions

class GameOverScreen(stats: GameStatistics) extends ScreenBehavior:
  override def writables: Seq[Writable] =
    Seq(
      Writable("Game Over", 0.4, 8.5, 1.4),
      Writable("Placed plants: " + stats.getPlants.size, ViewportWidth / 4, 6, 0.5),
      Writable("Killed zombies: " + stats.getZombies.size, ViewportWidth / 4, 5, 0.5),
      Writable("Reached wave: " + stats.rounds, ViewportWidth / 4, 4, 0.5)
    )
  override def actors: Seq[Actor] =
    val width = ViewportWidth/2
    val newGameButton: Actor = PulsingImageButton(texture(NewGameButton), ViewportWidth/2 - width/2, 0.2, width, HUDHeight*2)
    newGameButton onTouchUp Game.startNewGame
    Seq(newGameButton)

  override def viewport: Viewport = Game.viewport

/*
object EndGameMenu:
  def apply(stats: GameStatistics) = new EndGameMenu(stats)

  class EndGameMenu(stats: GameStatistics) extends ScreenAdapter :
    private lazy val stage = new Stage(Game.viewport)
    lazy val font: BitmapFont = BitmapFont(Gdx.files.internal("assets/gameWindow/font.fnt"), Gdx.files.internal("assets/gameWindow/font.png"), false)
    lazy val batch: SpriteBatch = SpriteBatch()

    override def render(delta: Float): Unit =
      Gdx.gl.glClearColor(0, 0, 0, 1)
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
      batch.begin()
      font.getData.setScale(0.05f)
      font.draw(batch, "Hai perso", ViewportWidth / 2.3f, ViewportHeight - HUDHeight / 2)
      //font.draw(batch, "Hai ucciso: " + stats.getZombies.size.toString, ViewportWidth.toFloat / 2.3f, ViewportHeight - HUDHeight / 3)
      //font.draw(batch, "Hai superato: " + stats.rounds.toString, ViewportWidth.toFloat / 2.3f, ViewportHeight - HUDHeight / 4)
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
*/
