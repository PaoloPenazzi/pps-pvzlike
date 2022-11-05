package model.common

import model.entities.Plant

import scala.annotation.targetName
import scala.concurrent.duration.FiniteDuration

object Utilities:

  enum Speed(val speed: FiniteDuration):
    case Slow extends Speed(FiniteDuration(32, "milliseconds"))
    case Normal extends Speed(FiniteDuration(16, "milliseconds"))
    case Fast extends Speed(FiniteDuration(8, "milliseconds"))

  enum Sun(val value: Int):
    case Small extends Sun(15)
    case Normal extends Sun(50)
    case Big extends Sun(100)

  case class MetaData(sun: Int = 0, velocity: Speed = Speed.Normal):
    @targetName("sum")
    def +(quantity: Int): MetaData = MetaData(quantity + sun, velocity)

    @targetName("subtraction")
    def -(quantity: Int): MetaData = MetaData(sun - quantity, velocity)

    @targetName("change velocity")
    def >>>(newVelocity: Speed): MetaData = MetaData(sun, newVelocity)

