package model

import model.Statistics.{GameStatistics, GameStats}
import model.entities.{BasicZombie, PeaShooter}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.language.postfixOps

class StatisticsTest extends AnyFlatSpec with Matchers:

  "Kill a zombie" should "increase the number" in {
    val stats = GameStatistics() playable BasicZombie()
    stats.entities shouldBe List(BasicZombie())
  }

  "Pass a round" should "increase the number of round" in {
    GameStatistics().increaseRound().rounds shouldBe 1
  }

  "At the end of the game" should "get only the zombies" in {
    val stats = GameStatistics() playable BasicZombie() playable BasicZombie() playable PeaShooter()
    stats.getZombies shouldBe List(BasicZombie(), BasicZombie())
  }

