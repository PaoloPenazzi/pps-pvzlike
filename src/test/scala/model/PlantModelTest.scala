package model

import model.entities.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.language.implicitConversions
import WorldSpace.{LanesLength, given}
import model.entities.TroopState.{Attacking, Dead, Idle}

import scala.concurrent.duration.FiniteDuration

class PlantModelTest extends AnyFlatSpec with should.Matchers:
  val testingLane = 1
  val otherLane = 2
  val peashooter: Troop = PeaShooter((testingLane, 10))
  val zombieInTheSameLane: Troop = Zombie((testingLane, 50))
  val zombieOutOfRange: Troop = Zombie((testingLane, 100))
  val lowHealthPeashooter: Troop = PeaShooter((1, LanesLength)) withLife 25
  val dummyPlant: Troop = PeaShooter((otherLane, 50))
  val dummyZombie: Troop = Zombie((otherLane, 50))
  val dummyBullet: Bullet = Paw(0,0)

  "A peashooter" should "be in Idle state if it can't attack any enemy" in {
    peashooter.update(FiniteDuration(16, "milliseconds"), List()).state shouldBe Idle
  }
  "A peashooter" should "enter attacking state if it can attack an enemy" in {
    peashooter.update(FiniteDuration(16, "milliseconds"), List(zombieInTheSameLane)).state shouldBe Attacking
  }
  "A peashooter" should "filter the interesting entities" in {
    val entitiesFiltered = List(dummyPlant, zombieInTheSameLane, dummyZombie).filter(peashooter.isInterestedIn)
    assertResult(entitiesFiltered)(List(zombieInTheSameLane))
  }
  "A peashooter" should "lose HPs after getting hit" in {
    val updatedTurret = peashooter collideWith dummyBullet
    updatedTurret.life should be < peashooter.life
  }
  "A peashooter" should "die if reaches 0 HP" in {
    (lowHealthPeashooter collideWith dummyBullet).state shouldBe Dead
  }