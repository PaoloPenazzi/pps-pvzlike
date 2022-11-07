package view

import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch, TextureRegion}
import com.badlogic.gdx.graphics.{GL20, Texture}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.{InputEvent, Stage}
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.{ClickListener, TextureRegionDrawable}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.{Gdx, ScreenAdapter}
import view.View.EntityRenderer
import view.ViewportSpace.*
import view.Sprites.{height, spriteName, width}
import model.entities.{CherryBomb, Entity, PeaBullet, Shooter, SnowBullet, Troop, Troops, Wallnut}
import model.common.Utilities.MetaData
import controller.ViewActor.sendPlacePlant
import ScalaGDX.*
import ScalaGDX.given

import scala.language.implicitConversions


object GameScreen:
  def apply() = new GameScreen()

class GameScreen() extends ScreenAdapter with EntityRenderer :
  private val camera = Game.viewport.getCamera
  private var entities: List[Entity] = List.empty

  private lazy val stage = new Stage(Game.viewport)
  var pendingPlant: Option[Troop] = None

  lazy val background: Texture = Texture(Gdx.files.classpath("assets/background/day.png"))
  lazy val font: BitmapFont = BitmapFont(Gdx.files.internal("assets/gameWindow/font.fnt"), Gdx.files.internal("assets/gameWindow/font.png"), false)
  lazy val batch: SpriteBatch = SpriteBatch()
  lazy val gamingWindowNumberOfSun: Texture = Texture(Gdx.files.classpath("assets/gameWindow/numberOfSun.png"))


  private var metaData: MetaData = MetaData()

  override def render(delta: Float): Unit =
    batch.setProjectionMatrix(camera.combined)
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    batch.begin()
    batch.draw(background, -3, 0, 25, ViewportHeight - HUDHeight)
    font.getData.setScale(.05f)
    font.draw(batch, metaData.sun.toString, 12, 7.5f)
    entities.foreach(e => batch.draw(texture(e), projectX(e.position.x), projectY(e.position.y), width(e), height(e)))
    batch.end()
    stage.draw(); //Draw the ui
    stage.act(delta)


  override def show(): Unit =
    stage.clear()
    val peashooterCard: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/peashooter-card.png"))
    val wallnutCard: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/wallnut-card.png"))
    val cherryBombCard: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/cherrybomb-card.png"))
    val snowshooterCard: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/snowshooter-card.png"))
    createButtonFromImage(peashooterCard, 0, ViewportHeight - HUDHeight, 1.5f, 1.2f)
    createButtonFromImage(wallnutCard, 1.5f, ViewportHeight - HUDHeight, 1.5f, 1.2f)
    createButtonFromImage(cherryBombCard, 3.0f, ViewportHeight - HUDHeight, 1.5f, 1.2f)
    createButtonFromImage(snowshooterCard, 4.5f, ViewportHeight - HUDHeight, 1.5f, 1.2f)
    Gdx.input.setInputProcessor(stage)
    stage.onTouchDown( pos =>
      for
        plant <- pendingPlant
        plantCoordinates <- plantCoordinates(pos)
      do
        sendPlacePlant(plant withPosition plantCoordinates)
      if pos.y < HUD.y then
        pendingPlant = None
    )

  private def createButtonFromImage(texture: Texture, x: Float, y: Float, width: Float, height: Float): ImageButton =
    val textureRegionDrawable = new TextureRegion(texture)
    val button = new ImageButton(new TextureRegionDrawable(textureRegionDrawable))
    button.setBounds(x, y, width, height)
    button.setTransform(true)
    button.onTouchDown(_ =>
        System.out.println("touchdown")
        button.clearActions()
        button.setScale(1.2f)
        texture.toString match
          case s if s.matches("""\S*peashooter\S*""") => pendingPlant = Option(Troops.shooterOf[PeaBullet])
          case s if s.matches("""\S*snowshooter\S*""") => pendingPlant = Option(Troops.shooterOf[SnowBullet])
          case s if s.matches("""\S*wallnut\S*""") => pendingPlant = Option(Troops.ofType[Wallnut])
          case s if s.matches("""\S*cherrybomb\S*""") => pendingPlant = Option(Troops.ofType[CherryBomb])
          case _ => pendingPlant = None
    )
    button.onTouchUp(() =>
          System.out.println("touchup")
          button.addAction(Actions.scaleTo(1f, 1f, 0.5f))
    )
    stage.addActor(button)
    button


  override def resize(width: Int, height: Int): Unit =
    Game.viewport.update(width, height)
    camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0)
    camera.update()

  def renderEntities(entities: List[Entity]): Unit = this.entities = entities

  def renderMetadata(metaData: MetaData): Unit = this.metaData = metaData

  val texture: Entity => Texture = memoizedTexture

  def memoizedTexture: Entity => Texture =
    def texture(entity: Entity): Texture = new Texture(Gdx.files.classpath("assets/" + spriteName(entity)))

    val cache = collection.mutable.Map.empty[String, Texture]

    entity =>
      val key = spriteName(entity)
      cache.getOrElse(key, {
        cache.update(key, texture(entity))
        cache(key)
      })
