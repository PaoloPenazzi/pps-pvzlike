package model.entities

object WorldSpace:
  given Conversion[(Int, Float), Position] = tuple => Position(tuple._1,tuple._2)
  
  val NumOfLanes: Int = 1
  val LanesLength: Float = 100

  case class Position(y: Int, x: Float)
  
  





