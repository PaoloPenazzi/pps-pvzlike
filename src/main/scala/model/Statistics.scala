package model

import model.entities.{BasicZombie, Entity, Plant, Zombie}

/** Wrapper of game statistics. */
object Statistics:

  /** Defines which insights we want to gather from the game. */
  trait GameStats:
    /**
     *
     * @return the entities played/killed in the game.
     */
    def entities: Seq[Entity]

    /**
     *
     * @return the number of rounds survived.
     */
    def rounds: Int

  /**
   * Defines which [[Zombie]]'s insights we want to gather from the game.
   */
  trait ZombieStatsOps:
    stats: GameStats =>
    /**
     *
     * @return the [[Zombie]]s killed.
     */
    def getZombies: Seq[Zombie] =
      stats.entities.foldLeft(List.empty)((acc, e) => e match {
        case zombie: Zombie => acc :+ zombie
        case _ => acc
      })

  /**
   * Defines which [[Plant]]'s insights we want to gather from the game.
   */
  trait PlantStatsOps:
    stats: GameStats =>
    /**
     *
     * @return the [[Plant]]s placed.
     */
    def getPlants: Seq[Plant] = stats.entities.collect { case e: Plant => e }

  trait GameStatsOps:
    /** Adds an [[Entity]] played/killed.
     *
     * @param entity the [[Entity]] that you want to add.
     * @return the statistics updated.
     */
    def played(entity: Entity): GameStats

    /** Increases the number of rounds of a certain quantity.
     *
     * @param r the number of rounds to increase.
     * @return the statistics updated.
     */
    def increaseRound(r: Int): GameStats

  /** Implements our standards statistics.
   *
   * @param entities the entities played/killed.
   * @param rounds   the number of rounds survived.
   */
  case class GameStatistics(entities: Seq[Entity] = List.empty, rounds: Int = 1) extends GameStats
    with GameStatsOps with ZombieStatsOps with PlantStatsOps :

    override def played(entity: Entity): GameStatistics = GameStatistics(entities :+ entity, rounds)

    override def increaseRound(r: Int = 1): GameStatistics = GameStatistics(entities, rounds + r)






