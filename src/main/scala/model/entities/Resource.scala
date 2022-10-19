package model.entities

import scala.annotation.targetName
import scala.concurrent.duration.FiniteDuration

trait Resource[T]:
  def value: T
  def duration: FiniteDuration

case class Sun(override val value: Int = 25,
               override val duration: FiniteDuration = FiniteDuration(5, "second")) extends Resource[Int]:
  @targetName("sum")
  def +(sun: Sun): Sun = Sun(this.value + sun.value)

case class MetaData()





