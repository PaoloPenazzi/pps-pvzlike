package model

import model.entities.{BasicZombie, Entity, Zombie}

object Statistics:

  trait GameStats:
    def entities: Seq[Entity]
    def rounds: Int

  trait ZombieStatsOps:
    stats: GameStats =>
    def getZombies: Seq[Zombie] =
      stats.entities.foldLeft(List.empty)((acc, e) => e match {
        case zombie: Zombie => acc :+ zombie
        case _ => acc
      })

  trait GameStatsOps:
    def playable(entity: Entity): GameStats
    def increaseRound(r: Int): GameStats

  case class GameStatistics(
                             entities: Seq[Entity] = List.empty,
                             rounds: Int = 0
                           ) extends GameStats with GameStatsOps with ZombieStatsOps:
    override def playable(entity: Entity): GameStatistics = GameStatistics(entities :+ entity, rounds)
    override def increaseRound(r: Int = 1): GameStatistics = GameStatistics(entities, rounds + r)






