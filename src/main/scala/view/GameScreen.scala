package view

import com.badlogic.gdx.scenes.scene2d.Actor
import view.View.Renderer
import view.ViewportSpace.*
import view.Sprites.{FastButton, GameBackground, NormalButton, PauseButton, ResumeButton, Sun, cardName, spriteName}
import model.entities.{CherryBomb, Entity, PeaBullet, Shooter, SnowBullet, Troop, Troops, Wallnut}
import model.common.Utilities.MetaData
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ScalaGDX.{Drawable, ImageButtons, Writable, given}
import ScalaGDX.Utils.texture
import ScalaGDX.ImageButtons.{builder, given}
import ScalaGDX.ActorBehaviors.*
import akka.actor.typed.ActorRef
import com.badlogic.gdx.scenes.scene2d.ui.{Image, ImageButton}
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import controller.{SendChangeGameSpeed, SendPauseGame, SendPlacePlant, SendResumeGame, ViewMessage}
import model.common.Utilities.Speed.*
import view.ScalaGDX.Screen.ScreenBehavior
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle

import scala.language.postfixOps
import scala.language.implicitConversions


class GameScreen(viewActor: ActorRef[ViewMessage]) extends ScreenBehavior with Renderer :
  private var pendingPlant: Option[Troop] = None
  private var entities: List[Entity] = List.empty
  private var metaData: MetaData = MetaData()

  override def drawables: Seq[Drawable] =
    def drawableEntities: Seq[Drawable] =
      // We render entities of lower lanes in a more foreground position, to better represent an isometric point of view
      given Ordering[Entity] = (e1, e2) => e2.position.y - e1.position.y
      entities.sorted.map(e => Drawable(spriteName(e), x(e), y(e), width(e), height(e)))

    Seq(
      Drawable(Sun, SunBoundaries),
      Drawable(GameBackground, BackgroundBoundaries)
    ) ++ drawableEntities

  override def writables: Seq[Writable] =
    Seq(Writable("=" + metaData.sun.toString, SunStringBoundaries))

  override def actors: Seq[Actor] =
    cards :+ pauseButton :+ speedUpButton


  override def onScreenTouch: Vector2 => Unit = pos =>
    for
      plant <- pendingPlant
      plantCoordinates <- plantCoordinates(pos)
    do
      viewActor ! SendPlacePlant(plant withPosition plantCoordinates)
    if pos.y < HUDBoundaries.y then
      pendingPlant = None

  override def viewport: Viewport = Game.viewport

  def renderEntities(entities: List[Entity]): Unit = this.entities = entities

  def renderMetadata(metaData: MetaData): Unit = this.metaData = metaData

  private def cards =
    val troops = Seq(Troops.shooterOf[PeaBullet], Troops.ofType[Wallnut], Troops.ofType[CherryBomb], Troops.shooterOf[SnowBullet])
    for
      (troop, x) <- troops zip (0 to troops.size).map(_ * CardWidth)
    yield
      val button: Actor = ImageButtons.builder withTexture texture(cardName(troop)) withBounds (x, ViewportHeight - HUDHeight, CardWidth, CardWidth)
      button.addPulseOnTouch()
      button.onTouchDown(_ => pendingPlant = Some(troop))
      button

  private def pauseButton =
    val style = ImageButtonStyle()
    style.up = TextureRegionDrawable(texture(PauseButton))
    style.checked = TextureRegionDrawable(texture(ResumeButton))

    val button: ImageButton = ImageButtons.builder withStyle style withBounds (13, 8, 3, 1)
    button.addPulseOnTouch()
    button.onTouchUp(() =>
      viewActor ! (if button.isChecked then SendPauseGame() else SendResumeGame())
    )
    button

  private def speedUpButton =
    val style = ImageButtonStyle()
    style.up = TextureRegionDrawable(texture(NormalButton))
    style.checked = TextureRegionDrawable(texture(FastButton))

    val button: ImageButton = ImageButtons.builder withStyle style withBounds(13, 6.5, 3, 2)
    button.addPulseOnTouch()
    button.onTouchUp(() =>
      val speed = if button.isChecked then Fast else Normal
      viewActor ! SendChangeGameSpeed(speed)
    )
    button
