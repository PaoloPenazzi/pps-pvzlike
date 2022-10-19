package model

import model.entities.{Enemy, Entity, PeaShooter, TroopState, Turret, WorldSpace, Zombie}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import WorldSpace.{LanesLength, given}
import model.entities.TroopState.*

import scala.concurrent.duration.FiniteDuration


class EnemyModelTest extends AnyFlatSpec with should.Matchers:

  "A zombie" should "enter attacking state if interests list is not empty" in {
    val turret: Turret = PeaShooter((1, 0))
    val zombie: Enemy =  Zombie((1, LanesLength / 2))
    zombie.state shouldBe Moving
    zombie.update(FiniteDuration(16, "milliseconds"), List(turret)).state shouldBe Attacking
  }
  "A zombie" should "not be in attacking state if interests list is empty" in {
    val zombie: Enemy = Zombie((2, LanesLength / 2))
    zombie.state shouldBe Moving
    zombie.update(FiniteDuration(16, "milliseconds"), List.empty).state should not be Attacking
  }
  "A enemy" should "filter the interesting entities" in {
    val basicZombie: Enemy = Zombie((1, LanesLength / 2))
    val firstTurretInFirstLane: Turret = PeaShooter((1, LanesLength))
    val secondTurretInSecondLane: Turret = PeaShooter((2, LanesLength * 0.75))
    val firstZombieFirstLane: Enemy = Zombie((1, LanesLength))
    val thirdZombieInSecondLane: Enemy = Zombie((2, LanesLength))
    val entities: List[Entity] = List(firstTurretInFirstLane, secondTurretInSecondLane,
      firstZombieFirstLane, thirdZombieInSecondLane)
    assert(entities.filter(basicZombie.isInterestedIn) == List(firstTurretInFirstLane))
  }


  import org.scalatest.funsuite.AnyFunSuite
  import org.scalatest.matchers.should.Matchers.*

  class EnemyTest extends AnyFunSuite:
      test("Enemy correctly defines constructors") {
        "Enemy(1, LanesLength / 2)" should compile
  }


