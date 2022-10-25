package view

import com.badlogic.gdx.math.{GridPoint2, Rectangle, Vector2}
import model.entities.WorldSpace.*

object ViewportSpace:
  def ViewportWidth = 16
  def ViewportHeight = 9
  def HUDHeight = 2f
  def HUD: Rectangle = Rectangle(0, ViewportHeight - HUDHeight, ViewportWidth, HUDHeight)
  def leftOffsetGrid = 1.4f
  def rightOffsetGrid = 1.4f
  def topOffsetGrid = 1
  def bottomOffsetGrid = 0.4f
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