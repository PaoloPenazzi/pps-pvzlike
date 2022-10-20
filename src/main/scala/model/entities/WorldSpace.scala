package model.entities

object WorldSpace:
  given Conversion[(Int, Float), Position] = tuple => Position(tuple._1,tuple._2)
  given Conversion[(Int, Int), Position] = tuple => Position(tuple._1,tuple._2.toFloat)
  given Conversion[(Int, Double), Position] = tuple => Position(tuple._1,tuple._2.toFloat)
  
  val NumOfLanes: Int = 1
  val LanesLength: Float = 90
  val cellLength = 10

  case class Position(y: Int, x: Float)
  
  





