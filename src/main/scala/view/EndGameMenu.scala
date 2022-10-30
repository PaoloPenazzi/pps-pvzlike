package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.{Gdx, ScreenAdapter}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera}
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.ScreenUtils
import controller.RootActor
import controller.RootActor.RootCommands.StartGame

object EndGameMenu :
  def apply() = new EndGameMenu()

class EndGameMenu() extends ScreenAdapter :
  lazy val font: BitmapFont = BitmapFont()

  private lazy val camera: OrthographicCamera = OrthographicCamera()

  override def render(delta: Float): Unit =
    Game.batch.begin()



    Game.batch.end()
