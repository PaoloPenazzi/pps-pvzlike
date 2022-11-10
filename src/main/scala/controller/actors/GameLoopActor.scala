package controller.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.actors.GameLoopActor.GameLoopCommands.*
import controller.utils.CollisionUtils.handleCollision
import controller.utils.GameLoopUtils.*
import model.GameData.GameEntity
import model.GameData.GameSeq.given
import model.Statistics.GameStatistics
import model.actors.{ModelMessage, TroopActor}
import model.common.Utilities.{MetaData, Speed, Sun}
import model.entities.{Bullet, Entity, Plant, Troop}
import view.Game
import view.actors.{GameOver, RenderEntities, RenderMetaData, ViewMessage}

import scala.language.{implicitConversions, postfixOps}

/** It's the actor responsible of system's update and deals with the incoming input from the view.
 *
 * Sends [[ModelMessage]] messages to the Model and [[ViewMessage]] messages to the View.
 * */
object GameLoopActor:

  def apply(
             viewActor: ActorRef[ViewMessage],
             entities: Seq[GameEntity[Entity]] = List.empty,
             metaData: MetaData = MetaData(),
             stats: GameStatistics = GameStatistics()
           ): Behavior[Command] =
    GameLoop(viewActor, entities, metaData, stats).standardBehavior()

  /** The GameLoop Actor: updates the whole game by exploiting the reactivity of an actor.
   * This actor sends a [[UpdateLoop]] message to itself with a determined delay
   * that depends on the a specific [[Speed]].
   *
   * @param viewActor the reference of the View Actor.
   * @param entities  the entities in game in a specific moment.
   * @param metaData  the meta-data of the game.
   * @param stats     the statistic of the game.
   */
  private case class GameLoop(
                               viewActor: ActorRef[ViewMessage],
                               entities: Seq[GameEntity[Entity]],
                               metaData: MetaData,
                               stats: GameStatistics
                             ):

    /** Provides the normal behavior of the GameLoop.
     *
     * @return a new updated GameLoop instance with a specific [[Behavior]].
     */
    def standardBehavior(): Behavior[Command] =
      Behaviors.withTimers(timer =>
        Behaviors.receive((ctx, msg) => {
          msg match
            case StartGame() =>
              startTimer(timer, UpdateLoop())
              startTimer(timer, UpdateResources(), metaData.speed.resourceSpeed)
              GameLoopActor(viewActor, createWave(ctx, stats.rounds), metaData, stats)

            case EndReached() => viewActor ! GameOver(stats); Behaviors.stopped

            case PauseGame() => pauseBehavior(metaData)

            case UpdateResources() =>
              startTimer(timer, UpdateResources(), metaData.speed.resourceSpeed)
              val updatedMetaData = metaData + Sun.Normal.value
              viewActor ! RenderMetaData(updatedMetaData, ctx.self)
              GameLoopActor(viewActor, entities, updatedMetaData, stats)

            case ChangeGameSpeed(speed) => GameLoopActor(viewActor, entities, metaData >>> speed, stats)

            case UpdateLoop() =>
              handleCollision(entities, ctx)
              val newStats = if isWaveOver(entities) then updateRoundStats(stats)
              else stats
              val newWave = if isWaveOver(entities) then createWave(ctx, newStats.rounds)
              else List.empty
              updateAll(ctx, metaData.speed, detectInterest(entities))
              startTimer(timer, UpdateLoop())
              GameLoopActor(viewActor, newWave ++ entities, metaData, newStats)

            case EntityUpdated(ref, entity) =>
              val newEntities = entities updateWith GameEntity(ref, entity)
              viewActor ! RenderEntities(newEntities.map(_.entity).toList, ctx.self)
              GameLoopActor(viewActor, newEntities, metaData, stats)

            case BulletSpawned(ref, bullet) => GameLoopActor(viewActor, entities :+ GameEntity(ref, bullet), metaData, stats)

            case PlacePlant(troop) =>
              troop.asInstanceOf[Plant] match
                case plant if canPlacePlant(plant, entities.of[Plant], metaData) =>
                  val newGameSeq = entities :+ GameEntity(ctx.spawnAnonymous(TroopActor(troop)), troop)
                  val newMetaData = metaData - troop.asInstanceOf[Plant].cost
                  val newStats = updateEntityStats(stats, troop)
                  viewActor ! RenderMetaData(newMetaData, ctx.self)
                  GameLoopActor(viewActor, newGameSeq, newMetaData, newStats)
                case _ => GameLoopActor(viewActor, entities, metaData, stats)

            case EntityDead(ref, entity) => GameLoopActor(viewActor, entities :- ref, metaData, entityDeadStats(stats, entity))

            case _ => Behaviors.same
        }))

    /** Provides the pause behavior of the GameLoop.
     *
     * @return a new updated GameLoop instance with a specific [[Behavior]].
     */
    def pauseBehavior(metaData: MetaData): Behavior[Command] =
      Behaviors.withTimers(timer =>
        Behaviors.receive((_, msg) => {
          msg match
            case ResumeGame() =>
              startTimer(timer, UpdateLoop(), metaData.speed.gameSpeed)
              startTimer(timer, UpdateResources(), metaData.speed.resourceSpeed)
              GameLoopActor(viewActor, entities, metaData, stats)

            case ChangeGameSpeed(speed) => pauseBehavior(metaData >>> speed)

            case _ => Behaviors.same
        }))

  /** The messages that [[GameLoop]] can handle. */
  object GameLoopCommands:
    trait Command

    case class StartGame() extends Command

    case class PauseGame() extends Command

    case class ResumeGame() extends Command

    case class UpdateLoop() extends Command

    case class EndReached() extends Command

    case class UpdateResources() extends Command

    case class ChangeGameSpeed(speed: Speed) extends Command

    case class EntityDead[E <: Entity](ref: ActorRef[ModelMessage], entity: Option[E]) extends Command

    case class EntityUpdated[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends Command

    case class BulletSpawned(ref: ActorRef[ModelMessage], bullet: Bullet) extends Command

    case class PlacePlant(troop: Troop) extends Command
