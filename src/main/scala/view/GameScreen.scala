package view

import com.badlogic.gdx.scenes.scene2d.Actor
import view.View.Renderer
import view.ViewportSpace.*
import view.Sprites.{GameBackground, Sun, cardName, height, spriteName, width}
import model.entities.{CherryBomb, Entity, PeaBullet, Shooter, SnowBullet, Troop, Troops, Wallnut}
import model.common.Utilities.MetaData
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import view.ScalaGDX.{Drawable, Writable}
import ScalaGDX.{PulsingImageButton, given}
import ScalaGDX.Utils.texture
import akka.actor.typed.ActorRef
import controller.{SendPlacePlant, ViewMessage}
import view.ScalaGDX.Screen.ScreenBehavior

import scala.language.implicitConversions


class GameScreen(viewActor: ActorRef[ViewMessage]) extends ScreenBehavior with Renderer :
  private var pendingPlant: Option[Troop] = None
  private var entities: List[Entity] = List.empty
  private var metaData: MetaData = MetaData()

  override def drawables: Seq[Drawable] =
    def drawableEntities: Seq[Drawable] =
      // The entities on lower lanes are rendered in a more foreground position, to better represent an isometric point of view
      given Ordering[Entity] = (e1, e2) => e2.position.y - e1.position.y
      entities.sorted.map(e => Drawable(spriteName(e), projectX(e.position.x), projectY(e.position.y), width(e), height(e)))

    Seq(
      Drawable(Sun, SunBoundaries),
      Drawable(GameBackground, BackgroundBoundaries)
    ) ++ drawableEntities

  override def writables: Seq[Writable] =
    Seq(Writable("=" + metaData.sun.toString, SunStringBoundaries))

  override def actors: Seq[Actor] =
    val troops = Seq(Troops.shooterOf[PeaBullet], Troops.ofType[Wallnut], Troops.ofType[CherryBomb], Troops.shooterOf[SnowBullet])

    for
      (troop, x) <- troops zip (0 to troops.size).map(_ * CardWidth)
    yield
      val button: Actor = PulsingImageButton(texture(cardName(troop)), x, ViewportHeight - HUDHeight, CardWidth, CardWidth)
      button.onTouchDown(_ => pendingPlant = Some(troop))
      button

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
