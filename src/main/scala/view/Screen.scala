package view

import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.{GL20, OrthographicCamera, Texture}
import com.badlogic.gdx.math.{Vector2, Vector3}
import com.badlogic.gdx.physics.box2d.{Box2DDebugRenderer, World}
import com.badlogic.gdx.utils.viewport.Viewport
import com.badlogic.gdx.{Gdx, Input, ScreenAdapter}
import model.entities.WorldSpace.{LanesLength, given}
import model.entities.*
import View.EntityRenderer
import Sprites.*
import ViewportSpace.*
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

object Screen:
  val Framerate: Float = 60

  def apply(viewport: Viewport) = new Screen(viewport)

class Screen(private val viewport: Viewport) extends ScreenAdapter with EntityRenderer :
  private val world: World = World(Vector2(0, 0), false)
  private val camera = viewport.getCamera
  private var entities: List[Entity] = List.empty
  private lazy val batch: SpriteBatch = SpriteBatch()


  override def render(delta: Float): Unit =
    world.step(1 / Screen.Framerate, 6, 2)
    batch.setProjectionMatrix(camera.combined)
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    val background: Texture = new Texture(Gdx.files.classpath("assets/background/day.png"))

    val gamingWindowPlants: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/pea-shooter-button.png"))
    val peaShooterButton = createButtonFromImage(gamingWindowPlants)
    val gamingWindowSunFlowers: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/sunflower-button.png"))
    val sunFlowerButton = createButtonFromImage(gamingWindowSunFlowers)
    val gamingWindowWallnut: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/wallnut-button.png"))
    val wallnutButton = createButtonFromImage(gamingWindowWallnut)
    val gamingWindowNumberOfSun: Texture = Texture(Gdx.files.classpath("assets/gameWindow/numberOfSun.png"))

    batch.begin()
    batch.draw(background, -3, projectY(0), 25, 7.5f)
    batch.draw(gamingWindowPlants, 0, 7.5f, 1.5f, 1.5f)
    batch.draw(gamingWindowSunFlowers, 1.5f, 7.5f, 1.5f, 1.5f)
    batch.draw(gamingWindowWallnut, 3, 7.5f, 1.5f, 1.5f)
    batch.draw(gamingWindowNumberOfSun, 12, 7.5f, 4, 1.5f)
    entities.foreach(e => batch.draw(texture(e), projectX(e.position.x), projectY(e.position.y), width(e), height(e)))

    batch.end()

  private def createButtonFromImage(texture: Texture): ImageButton =
    val textureRegion = new TextureRegion(texture)
    val regionDrawable = new TextureRegionDrawable(textureRegion)
    val button = new ImageButton(regionDrawable)
    button



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
        cache.update(entity.getClass.getSimpleName, texture(entity))
        cache(entity.getClass.getSimpleName)
      })


