package view

import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.g2d.TextureRegion
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
import view.Sprites.{height, width, spriteName}
import model.entities.{Entity, Troop, Troops, Wallnut, PeaShooter}
import model.common.Utilities.MetaData
import controller.ViewActor.sendPlacePlant




object GameScreen:
  val Framerate: Float = 60

  def apply() = new GameScreen()

class GameScreen() extends ScreenAdapter with EntityRenderer :
  private val world: World = World(Vector2(0, 0), false)
  private val camera = Game.viewport.getCamera
  private var entities: List[Entity] = List.empty

  private lazy val stage = new Stage(Game.viewport); //Set up a stage for the ui
  var pendingTroop: Option[Troop] = None

  lazy val background: Texture = Texture(Gdx.files.classpath("assets/background/day.png"))
  lazy val gamingWindowNumberOfSun: Texture = Texture(Gdx.files.classpath("assets/gameWindow/numberOfSun.png"))


  private var metaData: MetaData = MetaData()

  override def render(delta: Float): Unit =
    world.step(1 / GameScreen.Framerate, 6, 2)
    Game.batch.setProjectionMatrix(camera.combined)
    Gdx.gl.glClearColor(0, 0, 0, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    Game.batch.begin()
    Game.batch.draw(background, -3, 0, 25, ViewportHeight - HUDHeight)
    Game.font.getData.setScale(.05f)
    Game.font.draw(Game.batch, metaData.sun.toString, 12, 7.5f)
    //batch.draw(gamingWindowNumberOfSun, 10, ViewportHeight - HUDHeight, 6, HUDHeight)
    entities.foreach(e =>
      {if e.isInstanceOf[Wallnut] then updateTexture(e)
      Game.batch.draw(texture(e), projectX(e.position.x), projectY(e.position.y), width(e), height(e))})
    Game.batch.end()
    stage.draw(); //Draw the ui
    stage.act(delta)



  override def show(): Unit =
    stage.clear()
    val peashooterCard: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/peashooter-card.png"))
    val wallnutCard: Texture = new Texture(Gdx.files.classpath("assets/gameWindow/wallnut-card.png"))
    createButtonFromImage(peashooterCard, 0, ViewportHeight - HUDHeight, 1.5f, 1.2f)
    createButtonFromImage(wallnutCard, 1.5f, ViewportHeight - HUDHeight, 1.5f, 1.2f)
    Gdx.input.setInputProcessor(stage)
    stage.addListener(new ClickListener {
      override def touchDown(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Boolean =
        super.touchDown(event, x, y, pointerId, buttonId)
        if buttonId == Buttons.LEFT && pendingTroop.isDefined then
            plantCoordinates(Vector2(x,y)) foreach (t => sendPlacePlant(pendingTroop.get withPosition t))
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
          texture.toString match
            case s if s.matches("""\S*peashooter\S*""") => pendingTroop = Option(Troops.ofType[PeaShooter])
            case s if s.matches("""\S*wallnut\S*""") => pendingTroop = Option(Troops.ofType[Wallnut])
            case _ => pendingTroop = None
          true 
        else false  
      override def touchUp(event: InputEvent, x: Float, y: Float, pointerId: Int, buttonId: Int): Unit =
        super.touchUp(event, x, y, pointerId, buttonId)
        button.addAction(Actions.scaleTo(1f,1f, 0.5f))
      })
    stage.addActor(button)
    button



  override def resize(width: Int, height: Int): Unit =
    Game.viewport.update(width, height)
    camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0)
    camera.update()

  def renderEntities(entities: List[Entity]): Unit = this.entities = entities

  def renderMetadata(metaData: MetaData): Unit = this.metaData = metaData

  val texture: Entity => Texture = memoizedTexture

  def updateTexture(entity: Entity): Unit = cache.update(entity.getClass.getSimpleName,
    new Texture(Gdx.files.classpath("assets/" + spriteName(entity))))

  val cache = collection.mutable.Map.empty[String, Texture]

  def memoizedTexture: Entity => Texture =

    def texture(entity: Entity): Texture =
      val spritename = spriteName(entity)
      new Texture(Gdx.files.classpath("assets/" + spriteName(entity)))

    entity =>
        cache.getOrElse(entity.getClass.getSimpleName, {
        cache.update(entity.getClass.getSimpleName, texture(entity))
        cache(entity.getClass.getSimpleName)
      })


