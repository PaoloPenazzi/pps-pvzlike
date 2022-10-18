package model.entities

import scala.concurrent.duration.FiniteDuration

trait Resource[T]:
  def value: T
  def spawnTime: FiniteDuration

case class Sun(override val value: Int,
               override val spawnTime: FiniteDuration) extends Resource[Int]





