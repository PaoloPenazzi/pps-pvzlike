package view

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.physics.box2d.{Box2DDebugRenderer, World}
import com.badlogic.gdx.utils.viewport.Viewport
import com.badlogic.gdx.{Gdx, Input, ScreenAdapter}
import model.entities.*
import View.EntityRenderer

object Screen:
  val Framerate: Float = 60

  def apply(viewport: Viewport) = new Screen(viewport)

class Screen(private val viewport: Viewport) extends ScreenAdapter with EntityRenderer:
  private val world: World = World(Vector2(0, 0), false)
  private val camera = viewport.getCamera
  private var entities: List[Entity] = List.empty
  private val batch: SpriteBatch = SpriteBatch()

  override def render(delta: Float): Unit =
    world.step(1 / Screen.Framerate, 6, 2)

    batch.setProjectionMatrix(camera.combined)

    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    batch.begin()

    batch.draw(texture(Plant((1,1))), 0, 4.5f, 1, 1)

    batch.end()

  override def resize(width: Int, height: Int): Unit =
    viewport.update(width, height)
    camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0)
    camera.update()

  def renderEntities(entities: List[Entity]): Unit = this.entities = entities

  def texture(entity: Entity): Texture = new Texture(Gdx.files.classpath("assets/" + (entity match
    case _: Plant => "peashooter.png"
    case _: Seed => "seed.png"
    case _: Zombie => "zombie.png")))
