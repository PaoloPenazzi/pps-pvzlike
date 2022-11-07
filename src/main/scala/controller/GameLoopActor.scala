package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import controller.GameLoopActor.GameLoopCommands.{StartGame, UpdateLoop}
import controller.GameLoopActor.GameLoopCommands.*
import controller.GameLoopActor.GameLoopUtils.*
import controller.GameLoopActor.GameLoopUtils.CollisionUtils.*
import model.GameData
import model.GameData.{GameEntity, GameSeq}
import model.Statistics.GameStatistics
import model.actors.*
import model.common.Utilities.{MetaData, Speed, Sun}
import model.entities.*
import model.entities.WorldSpace.*
import model.waves.{Generator, WaveGenerator}
import view.Game

import scala.collection.immutable.Seq
import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.language.{implicitConversions, postfixOps}

/**
 * It's the actor responsible of system's update and deals with the incoming input from the view.
 * Send [[ModelMessage]] messages to the Model and [[ViewMessage]] messages to the View.
 * */
object GameLoopActor:

  val waveGenerator: WaveGenerator = Generator()
  def apply(
             viewActor: ActorRef[ViewMessage],
             entities: Seq[GameEntity[Entity]] = List.empty,
             metaData: MetaData = MetaData(),
             stats: GameStatistics = GameStatistics()
           ): Behavior[Command] = GameLoop(viewActor, entities, metaData, stats).standardBehavior()

  /**
   * The GameLoop Actor: update the whole game by exploiting the reactivity of an actor.
   * This actor sends a [[UpdateLoop]] message to itself with a determined delay
   * that depends on the a specific [[Speed]].
   *
   * @param viewActor the reference of the View Actor.
   * @param entities the entities in game in a specific moment.
   * @param metaData the meta-data of the game.
   * @param stats the statistic of the game.
   */
  case class GameLoop(
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
              waveGenerator.resetWaves()
              startTimer(timer, UpdateLoop())
              startTimer(timer, UpdateResources(), resourceTimer)
              Behaviors.same

            case GameOver() => Game.endGame(stats); Behaviors.stopped


            case PauseGame() => pauseBehavior()

            case UpdateResources() =>
              startTimer(timer, UpdateResources(), resourceTimer)
              GameLoopActor(viewActor, entities, metaData + Sun.Normal.value, stats)

            case ChangeGameSpeed(velocity) => GameLoopActor(viewActor, entities, metaData >>> velocity, stats)

            case UpdateLoop() =>
              checkCollision(entities, ctx)
              val newWave = if isWaveOver(entities) then createWave(ctx)
                            else List.empty
              val newStats = if isWaveOver(entities) then updateRoundStats(stats)
                             else stats
              updateAll(ctx, metaData.velocity, detectInterest(entities))
              startTimer(timer, UpdateLoop())
              GameLoopActor(viewActor, newWave ++ entities, metaData, newStats)

            case EntityUpdated(ref, entity) =>
              val newEntities = entities collect { case x if x.ref == ref => GameEntity(ref, entity) case x => x }
              render(ctx, viewActor, metaData, newEntities.map(_.entity).toList)
              GameLoopActor(viewActor, newEntities, metaData, stats)

            case BulletSpawned(ref, bullet) => GameLoopActor(viewActor, entities :+ GameEntity(ref, bullet), metaData, stats)

            case PlacePlant(troop) =>
              troop.asInstanceOf[Plant] match
                case plant if canPlacePlant(plant, entities, metaData) =>
                  val newGameSeq = entities :+ GameEntity(ctx.spawnAnonymous(TroopActor(troop)), troop)
                  val newMetaData = metaData - troop.asInstanceOf[Plant].cost
                  val newStats = updateEntityStats(stats, troop)
                  GameLoopActor(viewActor, newGameSeq, newMetaData, newStats)
                case _ => GameLoopActor(viewActor, entities, metaData, stats)

            case EntityDead(ref, entity) => GameLoopActor(viewActor, entities :- ref, metaData, entityDeadStats(stats, entity))

            case _ => Behaviors.same
        }))

    /** Provides the pause behavior of the GameLoop.
     *
     * @return a new updated GameLoop instance with a specific [[Behavior]].
     */
    def pauseBehavior(): Behavior[Command] =
      Behaviors.receive((ctx, msg) => {
        msg match
          case ResumeGame() =>
            ctx.self ! UpdateLoop()
            GameLoopActor(viewActor, entities, metaData, stats)

          case _ => Behaviors.same
      })

  /**
   * The messages that GameLoop can handle.
   */
  object GameLoopCommands:
    trait Command

    case class StartGame() extends Command

    case class PauseGame() extends Command

    case class ResumeGame() extends Command

    case class UpdateLoop() extends Command

    case class GameOver() extends Command

    case class UpdateResources() extends Command

    case class ChangeGameSpeed(velocity: Speed) extends Command

    case class EntityDead[E <: Entity](ref: ActorRef[ModelMessage], entity: Option[E]) extends Command

    case class EntityUpdated[E <: Entity](ref: ActorRef[ModelMessage], entity: E) extends Command

    case class BulletSpawned(ref: ActorRef[ModelMessage], bullet: Bullet) extends Command

    case class PlacePlant(troop: Troop) extends Command

  /**
   * An utility object for the GameLoop.
   */
  object GameLoopUtils:
    val resourceTimer: FiniteDuration = FiniteDuration(3, "seconds")

    /**
     * Starts a [[TimerScheduler]] and after [[time]] duration will send itself
     * a specific [[msg]].
     * @param timer the timer to start.
     * @param msg the message to send.
     * @param time the duration of the timer.
     */
    def startTimer(
                    timer: TimerScheduler[Command],
                    msg: Command,
                    time: FiniteDuration = Speed.Normal.speed
                  ): Unit =
      timer.startSingleTimer(msg, time)

    /**
     * Creates a new wave of [[Zombie]], for the next round.
     * @param ctx the context where to spawn the zombies actors.
     * @return a new wave (sequence) of zombies.
     */
    def createWave(ctx: ActorContext[Command]): Seq[GameEntity[Entity]] =
      waveGenerator.generateNextWave.enemies.map(e => GameEntity(ctx.spawnAnonymous(TroopActor(e)), e))

    /**
     * Checks if the [[entity]] dead was a [[Zombie]]
     * and, just in case it was, update the [[statistics]].
     * @param stats the statistics to update.
     * @param entity the entity removed from the game.
     * @return the statistics possibly updated.
     */
    def entityDeadStats(
                         stats: GameStatistics,
                         entity: Option[Entity]
                       ): GameStatistics =
      if entity.exists(_.isInstanceOf[Zombie]) then updateEntityStats(stats, entity.get) else stats

    /**
     * Updates the [[statistics]] related to the [[Entity]].
     * @param stats the statistics to update.
     * @param entity the entity which updates the statistics.
     * @return the statistics updated.
     */
    def updateEntityStats(
                           stats: GameStatistics,
                           entity: Entity
                         ): GameStatistics =
      stats played entity

    /**
     * Increases the [[statistics]] related to the game's [[round]].
     *
     * @param stats  the statistics to update.
     * @param r the number of rounds to add.
     * @return the statistics updated.
     */
    def updateRoundStats(
                     stats: GameStatistics,
                     r: Int = 1
                   ): GameStatistics =
      stats increaseRound r

    def canPlacePlant(
                       plant: Plant,
                       entities: Seq[GameEntity[Entity]],
                       metaData: MetaData
                     ): Boolean =
      isCellFree(plant, entities) && enoughSunFor(plant, metaData)

    def isCellFree(
                    plant: Troop,
                    entities: Seq[GameEntity[Entity]]
                  ): Boolean =
      !(entities map (_.entity) exists (_.position == plant.position))
    
    def enoughSunFor(
                      plant: Plant,
                      metaData: MetaData
                    ): Boolean =
      metaData.sun >= plant.cost

    def isWaveOver: Seq[GameEntity[Entity]] => Boolean = _ map (_.entity) collect { case enemy: Zombie => enemy } isEmpty

    def detectInterest(entities: Seq[GameEntity[Entity]]): Seq[(ActorRef[ModelMessage], Seq[Entity])] =
      for
        e <- entities
      yield
        (e.ref, for
          e2 <- entities
          if e != e2
          if e.entity isInterestedIn e2.entity
        yield e2.entity)

    def updateAll(
                   ctx: ActorContext[Command],
                   velocity: Speed,
                   interests: Seq[(ActorRef[ModelMessage], Seq[Entity])]
                 ): Unit =
      interests foreach (e => e._1 ! Update(velocity.speed, e._2.toList, ctx.self))

    def render(
                ctx: ActorContext[Command],
                viewActor: ActorRef[ViewMessage],
                metaData: MetaData,
                renderedEntities: List[Entity]
              ): Unit =
      viewActor ! Render(renderedEntities, ctx.self, metaData)

    object CollisionUtils:
      def checkCollision(
                          entities: Seq[GameEntity[Entity]],
                          ctx: ActorContext[Command]
                        ): Unit =
        detectCollision(entities) filter (_._2.nonEmpty) foreach { e =>
          if e._1.entity hitMultipleTimes
          then e._2 foreach { r => sendCollisionMessage(e._1, r, ctx); sendCollisionMessage(r, e._1, ctx) }
          else {
            sendCollisionMessage(e._1, e._2.head, ctx);
            sendCollisionMessage(e._2.head, e._1, ctx)
          }
        }

      def sendCollisionMessage[A <: Entity, E <: Entity](
                                                          to: GameEntity[A],
                                                          from: GameEntity[E],
                                                          ctx: ActorContext[Command]
                                                        ): Unit =
        to.ref ! Collision(from.entity, ctx.self)

      def detectCollision(entities: Seq[GameEntity[Entity]]): Seq[(GameEntity[Bullet], Seq[GameEntity[Troop]])] =
        import GameData.given
        for
          b <- entities.ofType[Bullet]
        yield
          (b, for
            e <- entities.ofType[Troop]
            if b.entity checkCollisionWith e.entity
          yield e)