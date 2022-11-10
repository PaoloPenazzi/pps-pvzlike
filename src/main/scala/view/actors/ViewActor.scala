package view.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.actors.GameLoopActor.GameLoopCommands.*
import controller.*
import model.Statistics.GameStatistics
import model.actors.ModelMessage
import model.common.Utilities.{MetaData, Speed}
import model.entities.{Entity, Plant, Troop}
import view.{Game, GameScreen}

trait ViewMessage

case class RenderEntities(entities: List[Entity], replyTo: ActorRef[Command]) extends ViewMessage

case class RenderMetaData(metaData: MetaData, replyTo: ActorRef[Command]) extends ViewMessage

case class SendPlacePlant(troop: Troop) extends ViewMessage

case class SendPauseGame() extends ViewMessage

case class SendResumeGame() extends ViewMessage

case class SendChangeGameSpeed(velocity: Speed) extends ViewMessage

case class GameOver(stats: GameStatistics) extends ViewMessage

object ViewActor:
  def apply(gameScreen: GameScreen, gameLoopActor: Option[ActorRef[Command]] = None): Behavior[ViewMessage] =
    Behaviors.receive((_, msg) => {
      msg match
        case RenderEntities(entities, replyTo) =>
          gameScreen.renderEntities(entities)
          ViewActor(gameScreen, Some(replyTo))

        case RenderMetaData(metaData, replyTo) =>
          gameScreen.renderMetadata(metaData)
          ViewActor(gameScreen, Some(replyTo))

        case GameOver(stats) =>
          gameScreen.gameOver(stats)
          Behaviors.same 

        case SendPlacePlant(troop) =>
          gameLoopActor.foreach(_ ! PlacePlant(troop))
          Behaviors.same

        case SendPauseGame() =>
          gameLoopActor.foreach(_ ! PauseGame())
          Behaviors.same

        case SendResumeGame() =>
          gameLoopActor.foreach(_ ! ResumeGame())
          Behaviors.same

        case SendChangeGameSpeed(speed) =>
          gameLoopActor.foreach(_ ! ChangeGameSpeed(speed))
          Behaviors.same
    })

  def apply(): Behavior[ViewMessage] =
    Behaviors.setup(ctx =>
      val gameScreen = GameScreen(ctx.self)
      Game.changeScreen(gameScreen)
      ViewActor(gameScreen)
    )



