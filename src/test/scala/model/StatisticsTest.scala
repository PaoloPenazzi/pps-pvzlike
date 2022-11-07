package model

import model.Statistics.{GameStatistics, GameStats}
import model.entities.{BasicZombie, Entity, PeaBullet, Troops, Troop}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.language.postfixOps

class StatisticsTest extends AnyFlatSpec with Matchers:

  val zombie: Troop = Troops.ofType[BasicZombie]
  val shooter: Troop = Troops.shooterOf[PeaBullet]

  "Kill a zombie" should "increase the number" in {
    val stats = GameStatistics() played zombie
    stats.entities shouldBe List(zombie)
  }

  "Pass a round" should "increase the number of round" in {
    GameStatistics().increaseRound().rounds shouldBe 1
  }

  "At the end of the game" should "get only the zombies" in {
    val stats = GameStatistics() played zombie played zombie played Troops.shooterOf[PeaBullet]
    stats.getZombies shouldBe List(zombie, zombie)
  }

  "get all zombies" should "work at the end-game" in {
    GameStatistics(List(zombie, zombie, shooter)).getZombies shouldBe List(zombie, zombie)
  }

  "get all plants" should "work at the end-game" in {
    GameStatistics(List(zombie, zombie, shooter)).getPlants shouldBe List(shooter)
  }

