package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.{ApplicationAdapter, Game, Gdx}
import controller.RootActor
import controller.RootActor.RootCommands.StartGame

import scala.language.implicitConversions


object Game extends com.badlogic.gdx.Game:
  val gameScreen: Screen = Screen(FitViewport(16,9))
  override def create(): Unit =
    setScreen(gameScreen)
    val system = ActorSystem(RootActor(), "launcher")
    system ! StartGame()
    

