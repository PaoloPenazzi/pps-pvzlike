package view

import com.badlogic.gdx.math.{GridPoint2, Rectangle, Vector2}
import model.entities.WorldSpace.*

object ViewportSpace:
  def ViewportWidth: Float = 16
  def ViewportHeight: Float = 9
  def HUDHeight: Float = 2
  def HUDBoundaries: Rectangle = Rectangle(0, ViewportHeight - HUDHeight, ViewportWidth, HUDHeight)
  def CardWidth: Float = 1.5
  def SunBoundaries: Rectangle = Rectangle(8.5, (HUDBoundaries.y + HUDHeight / 2) - 0.5f, 1, 1)
  def SunStringBoundaries: Rectangle = Rectangle(9.7, 7.75, 0, 0.8)
  def BackgroundBoundaries: Rectangle = Rectangle(-3, 0, 25, ViewportHeight - HUDHeight)
  def leftOffsetGrid: Float = 1.4
  def rightOffsetGrid: Float = 1.4
  def topOffsetGrid: Float = 1
  def bottomOffsetGrid: Float = 0.4
  def gridWidth: Float = ViewportWidth - leftOffsetGrid - rightOffsetGrid
  def gridHeight: Float = ViewportHeight - HUDHeight - bottomOffsetGrid - topOffsetGrid
  def grid: Rectangle = Rectangle(leftOffsetGrid, bottomOffsetGrid, gridWidth, gridHeight)
  val cells: Seq[Rectangle] =
    val cellWidth = gridWidth/(LanesLength/CellLength)
    val cellHeight = gridHeight/NumOfLanes
    for
      i <- 0 until LanesLength.toInt / CellLength.toInt
      j <- 0 until NumOfLanes
    yield
      Rectangle(i*cellWidth + leftOffsetGrid, j*cellHeight + bottomOffsetGrid, cellWidth, cellHeight)
  def projectX(x:Float): Float = (x + leftOffsetGrid * (LanesLength/gridWidth)) / LanesLength * gridWidth
  def projectY(y: Int): Float = (y + bottomOffsetGrid * (NumOfLanes/gridHeight)) / NumOfLanes * gridHeight
  def unprojectY(y: Float): Int = (y - bottomOffsetGrid * (NumOfLanes/gridHeight)).toInt
  def unprojectX(x: Float): Float = (x - leftOffsetGrid) * (LanesLength/gridWidth)
  def plantCoordinates(point: Vector2): Option[Position] =
    cells.find(_.contains(point)).map(cell => Position(unprojectY(cell.y), unprojectX(cell.x + cell.width/4)))