import akka.actor.typed.*
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration.DurationInt

trait TurretMessages
case class waveStarted() extends TurretMessages
case class waveFinished() extends TurretMessages
case class shoot() extends TurretMessages

object TurretActor:
  def apply(turret: Turret): Behavior[TurretMessages] =
    waitingForWave(turret)


  def waitingForWave(turret: Turret): Behavior[TurretMessages] =
    Behaviors.withTimers(timer => {
      Behaviors.receive { (context, message) =>
        message match
          case waveStarted() =>
            timer.startSingleTimer(shoot(), 2.seconds)
            waveIncoming(turret)
      }
    })

  def waveIncoming(turret: Turret): Behavior[TurretMessages] =
    print("changed behaviour")
    Behaviors.withTimers(timer => {
      Behaviors.receive { (context, message) =>
        message match
          case shoot() =>
            print("shoot")
            turret.isShooting = true
            timer.startSingleTimer(shoot(), turret.fireRate.seconds)
            Behaviors.same
          case waveFinished() =>
            turret.isShooting = false
            waitingForWave(turret)
      }
    })
