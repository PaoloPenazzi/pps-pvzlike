package model.entities

import scala.annotation.targetName
import scala.concurrent.duration.FiniteDuration

trait Resource[T]:
  def value: T
  def spawnTime: FiniteDuration

case class Sun(override val value: Int)(override val spawnTime: FiniteDuration = FiniteDuration(5, "seconds")) extends Resource[Int]:
  @targetName("sum")
  def -(sun: Sun): Sun = Sun(value - sun.value)()

  @targetName("subtraction")
  def +(sun: Sun): Sun = Sun(value + sun.value)()





