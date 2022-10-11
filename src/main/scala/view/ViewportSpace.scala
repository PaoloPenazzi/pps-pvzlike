package view

import model.entities.WorldSpace.{LanesLength, NumOfLanes}

object ViewportSpace {
  def ViewportWidth = 16
  def ViewportHeight = 9
  def HUDHeight = 4
  def projectX(x:Float): Float = x/LanesLength*ViewportWidth
  def projectY(y: Int): Float = y/NumOfLanes*(ViewportHeight - HUDHeight)
}
