package model.common

import model.entities.Turret

import scala.concurrent.duration.FiniteDuration

object Utilities:

  enum Velocity(val speed: FiniteDuration):
    case Slow extends Velocity(FiniteDuration(32, "milliseconds"))
    case Normal extends Velocity(FiniteDuration(16, "milliseconds"))
    case Fast extends Velocity(FiniteDuration(8, "milliseconds"))
    
  enum Sun(val value: Int):
    case Small extends Sun(15)
    case Normal extends Sun(25)
    case Big extends Sun(50)

  case class MetaData(sun: Int = 0, velocity: Velocity = Velocity.Normal)