package view

import com.badlogic.gdx.scenes.scene2d.Actor
import ViewportSpace.*
import Sprites.{FastButton, GameBackground, NormalButton, PauseButton, ResumeButton, Sun, cardName, spriteName}
import model.entities.{CherryBomb, Entity, PeaBullet, Shooter, SnowBullet, Troop, Troops, Wallnut}
import model.common.Utilities.MetaData
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import akka.actor.typed.ActorRef
import com.badlogic.gdx.scenes.scene2d.ui.{Image, ImageButton}
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import model.common.Utilities.Speed.*
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle
import model.Statistics.GameStatistics
import actors.{SendChangeGameSpeed, SendPauseGame, SendPlacePlant, SendResumeGame, ViewMessage}
import scalagdx.{Drawable, Writable}
import scalagdx.Utils.texture
import scalagdx.FadeWidget
import scalagdx.ActorBehaviors.*
import scalagdx.Screen.ScreenBehavior
import scalagdx.Clickable.given
import scalagdx.ImageButtons
import scalagdx.ImageButtons.given
import scala.language.postfixOps
import scala.language.implicitConversions


class GameScreen(viewActor: ActorRef[ViewMessage]) extends ScreenBehavior:
  private var pendingPlant: Option[Troop] = None
  private var entities: List[Entity] = List.empty
  private var metaData: MetaData = MetaData()
  private var faderOut: Option[FadeWidget] = None

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
    cards :+ pauseButton :+ speedUpButton :+ fadeIn :+ fadeOut
  
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

  def gameOver(stats: GameStatistics): Unit =
    faderOut.foreach(_.play(() =>
      Game.endGame(stats)))

  private def cards =
    val troops = Seq(Troops.shooterOf[PeaBullet], Troops.ofType[Wallnut], Troops.ofType[CherryBomb], Troops.shooterOf[SnowBullet])
    for
      (troop, x) <- troops zip (0 to troops.size).map(_ * CardWidth)
    yield
      val button: Actor = ImageButtons withSource texture(cardName(troop)) withBounds (x, ViewportHeight - HUDHeight, CardWidth, CardWidth)
      button.addPulseOnTouch()
      button.onTouchDown(_ => pendingPlant = Some(troop))
      button

  private def pauseButton =
    val style = ImageButtonStyle()
    style.up = TextureRegionDrawable(texture(PauseButton))
    style.checked = TextureRegionDrawable(texture(ResumeButton))

    val button: ImageButton = ImageButtons withSource style withBounds (13, 8, 3, 1)
    button.addPulseOnTouch()
    button.onTouchUp(() =>
      viewActor ! (if button.isChecked then SendPauseGame() else SendResumeGame())
    )
    button

  private def speedUpButton =
    val style = ImageButtonStyle()
    style.up = TextureRegionDrawable(texture(NormalButton))
    style.checked = TextureRegionDrawable(texture(FastButton))

    val button: ImageButton = ImageButtons withSource style withBounds(13.5, 7, 2, 1)
    button.addPulseOnTouch()
    button.onTouchUp(() =>
      val speed = if button.isChecked then Fast else Normal
      viewActor ! SendChangeGameSpeed(speed)
    )
    button

  private def fadeIn =
    FadeWidget(true, 1)

  private def fadeOut =
    faderOut = Some(FadeWidget(false, 1))
    faderOut.get