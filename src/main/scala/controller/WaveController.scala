package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

object WaveController:

  object WaveControllerCommands:
    sealed trait WaveControllerCommand extends Command
    case class GenerateWave(waveNumber: Int, enemiesNumber: Int, replyTo: ActorContext[Command])
    case class WaveStatus(replyTo: ActorContext[Command])

  object WaveControllerActor:

    import WaveControllerCommands.*

    def apply(): Behavior[Command] =
      Behaviors.setup { ctx => WaveControllerActor(ctx).standardBehavior() }

    private case class WaveControllerActor(ctx: ActorContext[Command]):
      def standardBehavior(): Behavior[Command] = Behaviors.receiveMessage(msg => {
        msg match
          case GenerateWave(waveNumber, enemiesNumber, replyTo) => ???
          case WaveStatus(replyTo) => ???
      })

