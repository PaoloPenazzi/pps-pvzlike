package model.common

import scala.concurrent.duration.FiniteDuration

object Utilities:

  enum Velocity(val speed: FiniteDuration):
    case Slow extends Velocity(FiniteDuration(32, "milliseconds"))
    case Normal extends Velocity(FiniteDuration(16, "milliseconds"))
    case Fast extends Velocity(FiniteDuration(8, "milliseconds"))
