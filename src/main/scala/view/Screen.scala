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
import com.badlogic.gdx.scenes.scene2d.ui.{HorizontalGroup, ImageButton, Table}
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener


object Screen:
  val Framerate: Float = 60

  def apply(viewport: Viewport) = new Screen(viewport)

class Screen(private val viewport: Viewport) extends ScreenAdapter with EntityRenderer :
  private val world: World = World(Vector2(0, 0), false)
  private val camera = viewport.getCamera
  private var entities: List[Entity] = List.empty
  private lazy val batch: SpriteBatch = SpriteBatch()
  private lazy val stage = new Stage(viewport); //Set up a stage for the ui
  var pendingTroop: Option[Troop] = None

  lazy val background: Texture = new Texture(Gdx.files.classpath("assets/background/day.png"))
  lazy val gamingWindowNumberOfSun: Texture = Texture(Gdx.files.classpath("assets/gameWindow/numberOfSun.png"))


  override def render(delta: Float): Unit =
    world.step(1 / Screen.Framerate, 6, 2)
    batch.setProjectionMatrix(camera.combined)
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    batch.begin()
    batch.draw(background, -3, 0, 25, ViewportHeight - HUDHeight)
    batch.draw(gamingWindowNumberOfSun, 10, ViewportHeight - HUDHeight, 6, HUDHeight)

    entities.foreach(e => batch.draw(texture(e), projectX(e.position.x), projectY(e.position.y), width(e), height(e)))

    batch.end()
    stage.draw(); //Draw the ui
    stage.act(delta)


  override def show(): Unit =
    stage.clear()
    val gamingWindowPlants: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/pea-shooter-button.png"))
    createButtonFromImage(gamingWindowPlants, 0, ViewportHeight - HUDHeight, 1.5f, 1.2f)
    Gdx.input.setInputProcessor(stage)
    stage.addListener(new ClickListener {
      override def touchDown(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Boolean =
        super.touchDown(event, x, y, pointerId, buttonId)
        if buttonId == Buttons.LEFT && pendingTroop.isDefined then
            turretCoordinates(Vector2(x,y)) foreach System.out.println
        if y < HUD.y then
          pendingTroop = None
        true
    })

  private def createButtonFromImage(texture: Texture, x: Float, y: Float, width: Float, height: Float): ImageButton =
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
          pendingTroop = Option(PeaShooter((0,0)))
          true 
        else false  
      override def touchUp(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Unit =
        super.touchUp(event, x, y, pointerId, buttonId)
        button.addAction(Actions.scaleTo(1f,1f, 0.5f))
      })
    stage.addActor(button)
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