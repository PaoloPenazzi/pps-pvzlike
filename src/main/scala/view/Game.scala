package view

import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.{Color, GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.{ApplicationAdapter, Game, Gdx}

import scala.language.implicitConversions


object Game extends com.badlogic.gdx.Game:
  override def create(): Unit =
    setScreen(Screen(FitViewport(16,9)))
    

