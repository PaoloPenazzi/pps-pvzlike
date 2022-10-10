package view

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.physics.box2d.{Box2DDebugRenderer, World}
import com.badlogic.gdx.utils.viewport.Viewport
import com.badlogic.gdx.{Gdx, Input, ScreenAdapter}
import model.entities.WorldSpace.{LanesLength, given}
import model.entities.*
import View.EntityRenderer
import Sprites.spriteName

object Screen:
  val Framerate: Float = 60

  def apply(viewport: Viewport) = new Screen(viewport)

class Screen(private val viewport: Viewport) extends ScreenAdapter with EntityRenderer:
  private val world: World = World(Vector2(0, 0), false)
  private val camera = viewport.getCamera
  private var entities: List[Entity] = List.empty
  private lazy val batch: SpriteBatch = SpriteBatch()

  override def render(delta: Float): Unit =
    world.step(1 / Screen.Framerate, 6, 2)

    batch.setProjectionMatrix(camera.combined)

    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    batch.begin()

    entities.foreach(e => batch.draw(texture(e), xToScreen(e.position.x), e.position.y, width(e), height(e)))

    batch.end()

  override def resize(width: Int, height: Int): Unit =
    viewport.update(width, height)
    camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0)
    camera.update()

  def renderEntities(entities: List[Entity]): Unit = this.entities = entities

  val texture: Entity => Texture = memoizedTexture
  def memoizedTexture: Entity => Texture =
    def texture(entity: Entity): Texture = new Texture(Gdx.files.classpath("assets/" + spriteName(entity)))

    val cache = collection.mutable.Map.empty[String, Texture]

    entity =>
      cache.getOrElse(entity.getClass.getSimpleName, {
        System.out.println("ciao")
        cache.update(entity.getClass.getSimpleName, texture(entity))
        cache(entity.getClass.getSimpleName)
      })

  def width(entity: Entity): Float = entity match
    case _: Plant => 1
    case _: Seed => 0.2
    case _: Zombie => 1

  def height(entity: Entity): Float = entity match
    case _: Plant => 1
    case _: Seed => 0.2
    case _: Zombie => 1.5


  def xToScreen(x:Float) = x/LanesLength*16