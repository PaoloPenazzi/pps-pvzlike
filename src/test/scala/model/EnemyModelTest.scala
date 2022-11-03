package model


import model.entities.{Bullet, FastZombie, PeaBullet, PeaShooter, Troop, Troops, BasicZombie, WarriorZombie}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import model.entities.TroopState.*

import scala.concurrent.duration.FiniteDuration


class EnemyModelTest extends AnyFlatSpec with should.Matchers:

  private val testingLane = 1
  private val otherLane = 2
  private val zombie: Troop = Troops.ofType[BasicZombie] withPosition (testingLane, 80)
  private val fastZombie: Troop = Troops.ofType[FastZombie] withPosition (testingLane, 80)
  private val warriorZombie: Troop = Troops.ofType[WarriorZombie] withPosition (testingLane, 80)

  private val plantInOtherLane: Troop = Troops.ofType[PeaShooter] withPosition (otherLane, 10)
  private val plantInTheSameLane: Troop = Troops.ofType[PeaShooter] withPosition (testingLane, 10)
  private val plantInRange: Troop = Troops.ofType[PeaShooter] withPosition (testingLane, 75)

  private val dummyPlant: Troop = Troops.ofType[PeaShooter] withPosition (otherLane, 50)
  private val dummyZombie: Troop = Troops.ofType[BasicZombie] withPosition (otherLane, 50)
  private val dummyBullet: Bullet = PeaBullet(0,0)


  "A BasicZombie" should "be in Moving state if it can't attack any plant" in {
    zombie.update(FiniteDuration(16, "milliseconds"), List()).state shouldBe Moving
  }
  "A BasicZombie" should "enter attacking state if it can attack an enemy" in {
    zombie.update(FiniteDuration(16, "milliseconds"), List(plantInTheSameLane)).state shouldBe Attacking
  }
  "A BasicZombie" should "filter the interesting entities" in {
    List(dummyPlant, plantInTheSameLane, dummyZombie, dummyBullet, plantInRange) filter zombie.isInterestedIn shouldBe List(plantInRange)
  }
  "A BasicZombie" should "lose HPs after getting hit" in {
    (zombie collideWith dummyBullet).life should be < zombie.life
  }
  "A BasicZombie" should "die if reaches 0 or less HP" in {
    (zombie withLife 25 collideWith dummyBullet).state shouldBe Dead
  }
  "A BasicZombie" should "not have interests" in {
    List(dummyZombie, plantInTheSameLane, dummyBullet) filter zombie.isInterestedIn shouldBe List.empty
  }

  "A FastZombie" should "be in Moving state if it can't attack any plant" in {
    fastZombie.update(FiniteDuration(16, "milliseconds"), List()).state shouldBe Moving
  }
  "A FastZombie" should "enter attacking state if it can attack an enemy" in {
    fastZombie.update(FiniteDuration(16, "milliseconds"), List(plantInTheSameLane)).state shouldBe Attacking
  }
  "A FastZombie" should "filter the interesting entities" in {
    List(dummyPlant, plantInTheSameLane, dummyZombie, dummyBullet, plantInRange) filter fastZombie.isInterestedIn shouldBe List(plantInRange)
  }
  "A FastZombie" should "lose HPs after getting hit" in {
    (fastZombie collideWith dummyBullet).life should be < fastZombie.life
  }
  "A FastZombie" should "die if reaches 0 or less HP" in {
    (fastZombie withLife 25 collideWith dummyBullet).state shouldBe Dead
  }
  "A FastZombie" should "not have interests" in {
    List(dummyZombie, plantInTheSameLane, dummyBullet) filter fastZombie.isInterestedIn shouldBe List.empty
  }

  "A WarriorZombie" should "be in Moving state if it can't attack any plant" in {
    warriorZombie.update(FiniteDuration(16, "milliseconds"), List()).state shouldBe Moving
  }
  "A WarriorZombie" should "enter attacking state if it can attack an enemy" in {
    warriorZombie.update(FiniteDuration(16, "milliseconds"), List(plantInTheSameLane)).state shouldBe Attacking
  }
  "A WarriorZombie" should "filter the interesting entities" in {
    println(warriorZombie.isInterestedIn(plantInRange))
    List(dummyPlant, plantInTheSameLane, dummyZombie, dummyBullet, plantInRange) filter warriorZombie.isInterestedIn shouldBe List(plantInRange)
  }
  "A WarriorZombie" should "lose HPs after getting hit" in {
    (warriorZombie collideWith dummyBullet).life should be < warriorZombie.life
  }
  "A WarriorZombie" should "die if reaches 0 or less HP" in {
    (warriorZombie withLife 25 collideWith dummyBullet).state shouldBe Dead
  }
  "A WarriorZombie" should "not have interests" in {
    List(dummyZombie, plantInTheSameLane, dummyBullet) filter warriorZombie.isInterestedIn shouldBe List.empty
  }



  import org.scalatest.funsuite.AnyFunSuite
  import org.scalatest.matchers.should.Matchers.*

  class EnemyTest extends AnyFunSuite:
      test("Enemy correctly defines constructors") {
        "Enemy(1, LanesLength / 2)" should compile
  }


