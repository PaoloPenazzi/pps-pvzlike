
package model

import model.entities.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.language.implicitConversions
import WorldSpace.{LanesLength, given}
import model.entities.TroopState.{Attacking, Dead}

import scala.concurrent.duration.FiniteDuration

class TurretModelTest extends AnyFlatSpec with should.Matchers:
  val testTurret: Turret = PeaShooter((1, LanesLength / 2))
  val lowHealthTurret: Turret = PeaShooter((1, LanesLength), 25)
  val dummyTurret1: Turret = PeaShooter((2, LanesLength))
  val dummyZombie1: Enemy = Zombie((1, LanesLength))
  val dummyZombie2: Enemy = Zombie((2, LanesLength))
  val dummyBullet: Bullet = PeaBullet(0,0)

  "A turret" should "enter attacking state if interests list is not empty" in {
      testTurret.update(FiniteDuration(16, "milliseconds"), List(dummyZombie1)).state shouldBe Attacking
  }
  "A turret" should "filter the interesting entities" in {
    val entities: List[Entity] = List(dummyTurret1, dummyZombie1, dummyZombie2)
    val entitiesFiltered = entities.filter(testTurret.isInterestedIn)
    assertResult(entitiesFiltered)(List(dummyZombie1))
  }
  "A turret" should "lose HPs after getting hitted" in {
    val updatedTurret = testTurret collideWith dummyBullet
    updatedTurret.life should be < testTurret.life
  }
  "A turret" should "die if reaches 0 HP" in {
    val updatedTurret = lowHealthTurret collideWith dummyBullet
    updatedTurret.state shouldBe Dead
  }