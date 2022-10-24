package model

import model.entities.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.language.{implicitConversions, postfixOps}
import WorldSpace.{LanesLength, given}
import model.entities.TroopState.{Attacking, Dead, Idle}

import scala.concurrent.duration.FiniteDuration

class PlantModelTest extends AnyFlatSpec with should.Matchers:
  val testingLane = 1
  val otherLane = 2
  val peashooter: Troop = Troops.ofType[PeaShooter] withPosition (testingLane, 10)
  val wallnut: Troop = Troops.ofType[Wallnut] withPosition (testingLane, 10)
  val zombieInTheSameLane: Troop = Troops.ofType[Zombie] withPosition (testingLane, 50)
  val zombieOutOfRange: Troop = Troops.ofType[Zombie] withPosition (testingLane, 100)
  val dummyPlant: Troop = Troops.ofType[PeaShooter] withPosition (otherLane, 50)
  val dummyZombie: Troop = Troops.ofType[Zombie] withPosition (otherLane, 50)
  val dummyBullet: Bullet = Paw(0,0)

  "A Peashooter" should "be in Idle state if it can't attack any enemy" in {
    peashooter.update(FiniteDuration(16, "milliseconds"), List()).state shouldBe Idle
  }
  "A Peashooter" should "enter attacking state if it can attack an enemy" in {
    peashooter.update(FiniteDuration(16, "milliseconds"), List(zombieInTheSameLane)).state shouldBe Attacking
  }
  "A Peashooter" should "filter the interesting entities" in {
    List(dummyPlant, zombieInTheSameLane, dummyZombie) filter peashooter.isInterestedIn shouldBe List(zombieInTheSameLane)
  }
  "A Peashooter" should "lose HPs after getting hit" in {
    (peashooter collideWith dummyBullet).life should be < peashooter.life
  }
  "A Peashooter" should "die if reaches 0 or less HP" in {
    (peashooter withLife 15 collideWith dummyBullet).state shouldBe Dead
  }
  "A Wallnut" should "not have interests" in {
    List(dummyPlant, zombieInTheSameLane, dummyZombie) filter wallnut.isInterestedIn shouldBe List.empty
  }
  "A Wallnut" should "lose HPs after getting hit" in {
    (wallnut collideWith dummyBullet).life should be < wallnut.life
  }
  "A Wallnut" should "die if reaches 0 or less HP" in {
    (wallnut withLife 20 collideWith dummyBullet).state shouldBe Dead
  }