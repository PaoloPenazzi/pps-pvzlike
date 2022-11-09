package controller.utils

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.{ActorContext, TimerScheduler}
import controller.actors.GameLoopActor.GameLoopCommands.Command
import model.GameData.GameEntity
import model.Statistics.GameStatistics
import model.actors.{ModelMessage, TroopActor, Update}
import model.common.Utilities.{MetaData, Speed}
import model.entities.{Entity, Plant, Troop, Zombie}
import model.waves.WaveGenerator

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

/** An utility object for the GameLoop. */
object GameLoopUtils:

  /** Starts a [[TimerScheduler]] and after [[time]] duration will send itself
   * a specific [[msg]].
   *
   * @param timer the timer to start.
   * @param msg   the message to send.
   * @param time  the duration of the timer.
   */
  def startTimer(timer: TimerScheduler[Command], msg: Command, time: FiniteDuration = Speed.Normal.gameSpeed): Unit =
    timer.startSingleTimer(msg, time)

  /** Creates a new wave of [[Zombie]], for the next round.
   *
   * @param ctx   the context where to spawn the zombies actors.
   * @param round the number of rounds.
   * @return a new wave (sequence) of zombies.
   */
  def createWave(ctx: ActorContext[Command], round: Int): Seq[GameEntity[Entity]] =
    WaveGenerator.generateNextWave(round).enemies.map(e => GameEntity(ctx.spawnAnonymous(TroopActor(e)), e))

  /** Checks if the [[entity]] dead was a [[Zombie]]
   * and, just in case it was, update the [[statistics]].
   *
   * @param stats  the statistics to update.
   * @param entity the entity removed from the game.
   * @return the statistics possibly updated.
   */
  def entityDeadStats(stats: GameStatistics, entity: Option[Entity]): GameStatistics =
    if entity.exists(_.isInstanceOf[Zombie]) then updateEntityStats(stats, entity.get) else stats

  /** Updates the [[statistics]] related to the [[Entity]].
   *
   * @param stats  the statistics to update.
   * @param entity the entity which updates the statistics.
   * @return the statistics updated.
   */
  def updateEntityStats(stats: GameStatistics, entity: Entity): GameStatistics =
    stats played entity

  /** Increases the [[statistics]] related to the game's [[round]].
   *
   * @param stats the statistics to update.
   * @param r     the number of rounds to add.
   * @return the statistics updated.
   */
  def updateRoundStats(stats: GameStatistics, r: Int = 1): GameStatistics =
    stats increaseRound r

  /** Checks if a [[plant]] is placeable.
   *
   * @param plant    the plant to place.
   * @param entities the entities in game.
   * @param metaData the meta-data's game.
   * @return [[true]] if the plant is placeable, [[false]] otherwise.
   */
  def canPlacePlant(plant: Plant, entities: Seq[GameEntity[Plant]], metaData: MetaData): Boolean =
    isCellFree(plant, entities) && enoughSunFor(plant, metaData)

  /** Checks if a cell is free.
   *
   * @param plant    the plant to place.
   * @param entities the entities in game.
   * @return [[true]] if the cell is free, [[false]] otherwise.
   */
  def isCellFree(plant: Troop, entities: Seq[GameEntity[Plant]]): Boolean =
    !(entities exists (_.entity.position == plant.position))

  /** Checks if the user has enough sun/resources to place the plant.
   *
   * @param plant    the plant to place.
   * @param metaData the meta-data's game.
   * @return [[true]] if the user has enough sun, [[false]] otherwise.
   */
  def enoughSunFor(plant: Plant, metaData: MetaData): Boolean =
    metaData.sun >= plant.cost

  /** Checks if the wave is over.
   *
   * @return [[true]] if the wave is over, [[false]] otherwise.
   */
  def isWaveOver: Seq[GameEntity[Entity]] => Boolean = _ map (_.entity) collect { case enemy: Zombie => enemy } isEmpty

  /** Create a [[Seq]] of possible [[Entity]] that can modify the internal state of [[Entity]].
   * In this context we call them [[interests]]: through these, the
   * model of each [[Entity]] can change in a certain way.
   *
   * @param entities the entities in game.
   * @return a [[Seq]] of all [[Entity]] affected by each [[Entity]] in the game.
   */
  def detectInterest(entities: Seq[GameEntity[Entity]]): Seq[(ActorRef[ModelMessage], Seq[Entity])] =
    for
      e <- entities
    yield
      (e.ref, for
        e2 <- entities
        if e != e2
        if e.entity isInterestedIn e2.entity
      yield e2.entity)

  /** Sends [[Update]] messages to all entities in game.
   *
   * @param ctx       the [[ActorContext]] of the sender, who wants the replies.
   * @param speed     the game update's frequency.
   * @param interests the interests for each [[Entity]] in game.
   */
  def updateAll(ctx: ActorContext[Command], speed: Speed, interests: Seq[(ActorRef[ModelMessage], Seq[Entity])]): Unit =
    interests foreach (e => e._1 ! Update(speed.gameSpeed, e._2.toList, ctx.self))
