package view

import com.badlogic.gdx.math.{GridPoint2, Rectangle, Vector2}
import model.entities.{Bullet, Entity, PawBullet, PeaBullet, SnowBullet, SwordBullet, Zombie}
import model.entities.WorldSpace.*

/**
 * Contains configuration of objects boundary and position inside the viewport space.
 * This includes projecting the model measures into the viewport.
 */
object ViewportSpace:
  def ViewportWidth: Float = 16
  def ViewportHeight: Float = 9
  def HUDHeight: Float = 2
  def HUDBoundaries: Rectangle = Rectangle(0, ViewportHeight - HUDHeight, ViewportWidth, HUDHeight)
  def CardWidth: Float = 1.5
  def SunBoundaries: Rectangle = Rectangle(6.5, (HUDBoundaries.y + HUDHeight / 2) - 0.5f, 1, 1)
  def SunStringBoundaries: Rectangle = Rectangle(7.7, 7.75, 0, 0.8)
  def BackgroundBoundaries: Rectangle = Rectangle(-3, 0, 25, ViewportHeight - HUDHeight)

  /**
   * Given a position, finds out if it's inside a cell of the game grid.
   * If it is, returns the position within the cell where a new plant can be placed.
   * If it's not, returns None.
   */
  def plantCoordinates(pos: Vector2): Option[Position] =
    cells.find(_.contains(pos)).map(cell => Position(unprojectY(cell.y), unprojectX(cell.x + cell.width / 4)))

  /**
   * @return the entity x relative to viewport.
   */
  def x(entity: Entity): Float = projectX(entity.position.x)

  /**
   * @return the entity y relative to viewport.
   */
  def y(entity: Entity): Float = projectY(entity.position.y) + yOffset(entity)

  /**
   * @return the entity width relative to viewport.
   */
  def width(entity: Entity): Float = entity.width * (gridWidth / LanesLength)

  /**
   * @return the entity height relative to viewport.
   */
  def height(entity: Entity): Float = entity match
    case _: PeaBullet => 0.4
    case _: PawBullet => 0.4
    case _: SnowBullet => 0.4
    case _: SwordBullet => 0.7
    case _: Zombie => 1.4
    case _ => 1

  private def leftOffsetGrid: Float = 1.4
  private def rightOffsetGrid: Float = 1.4

  private def topOffsetGrid: Float = 1

  private def bottomOffsetGrid: Float = 0.4

  private def gridWidth: Float = ViewportWidth - leftOffsetGrid - rightOffsetGrid

  private def gridHeight: Float = ViewportHeight - HUDHeight - bottomOffsetGrid - topOffsetGrid
  private def cells: Seq[Rectangle] =
    val cellWidth = gridWidth / (LanesLength / CellLength)
    val cellHeight = gridHeight / NumOfLanes
    for
      i <- 0 until LanesLength.toInt / CellLength.toInt
      j <- 0 until NumOfLanes
    yield
      Rectangle(i * cellWidth + leftOffsetGrid, j * cellHeight + bottomOffsetGrid, cellWidth, cellHeight)
  private def projectX(x: Float): Float = (x + leftOffsetGrid * (LanesLength / gridWidth)) / LanesLength * gridWidth

  private def projectY(y: Int): Float = (y + bottomOffsetGrid * (NumOfLanes / gridHeight)) / NumOfLanes * gridHeight

  private def unprojectY(y: Float): Int = (y - bottomOffsetGrid * (NumOfLanes / gridHeight)).toInt

  private def unprojectX(x: Float): Float = (x - leftOffsetGrid) * (LanesLength / gridWidth)
  private def yOffset(entity: Entity): Float = entity match
    case _: Bullet => 0.6
    case _ => 0

