package model


import model.entities.{Bullet, PeaBullet, PeaShooter, Troop, Troops, Zombie}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import model.entities.TroopState.*

import scala.concurrent.duration.FiniteDuration


class EnemyModelTest extends AnyFlatSpec with should.Matchers:

  private val testingLane = 1
  private val otherLane = 2
  private val zombie: Troop = Troops.ofType[Zombie] withPosition (testingLane, 80)

  private val plantInOtherLane: Troop = Troops.ofType[PeaShooter] withPosition (otherLane, 10)
  private val plantInTheSameLane: Troop = Troops.ofType[PeaShooter] withPosition (testingLane, 10)
  private val plantInRange: Troop = Troops.ofType[PeaShooter] withPosition (testingLane, 70)

  private val dummyPlant: Troop = Troops.ofType[PeaShooter] withPosition (otherLane, 50)
  private val dummyZombie: Troop = Troops.ofType[Zombie] withPosition (otherLane, 50)
  private val dummyBullet: Bullet = PeaBullet(0,0)


  "A zombie" should "be in Moving state if it can't attack any plant" in {
    zombie.update(FiniteDuration(16, "milliseconds"), List()).state shouldBe Moving
  }
  "A zombie" should "enter attacking state if it can attack an enemy" in {
    zombie.update(FiniteDuration(16, "milliseconds"), List(plantInTheSameLane)).state shouldBe Attacking
  }
  "A zombie" should "filter the interesting entities" in {
    println(zombie.isInterestedIn(plantInRange))
    List(dummyPlant, plantInTheSameLane, dummyZombie, dummyBullet, plantInRange) filter zombie.isInterestedIn shouldBe List(plantInRange)
  }
  "A zombie" should "lose HPs after getting hit" in {
    (zombie collideWith dummyBullet).life should be < zombie.life
  }
  "A Zombie" should "die if reaches 0 or less HP" in {
    (zombie withLife 25 collideWith dummyBullet).state shouldBe Dead
  }
  "A zombie" should "not have interests" in {
    List(dummyZombie, plantInTheSameLane, dummyBullet) filter zombie.isInterestedIn shouldBe List.empty
  }



  import org.scalatest.funsuite.AnyFunSuite
  import org.scalatest.matchers.should.Matchers.*

  class EnemyTest extends AnyFunSuite:
      test("Enemy correctly defines constructors") {
        "Enemy(1, LanesLength / 2)" should compile
  }


