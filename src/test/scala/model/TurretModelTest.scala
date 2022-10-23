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
  val turret: Troop = PeaShooter((1, 50))
  val lowHealthTurret: Troop = PeaShooter((1, LanesLength), 25)
  val dummyZombie1: Troop = Zombie((1, 60))
  val dummyTurret2: Troop = PeaShooter((2, LanesLength))
  val dummyZombie2: Troop = Zombie((2, LanesLength))
  val dummyBullet: Bullet = PeaBullet(0,0)

  "A turret" should "enter attacking state if interests list is not empty" in {
      turret.update(FiniteDuration(16, "milliseconds"), List(dummyZombie1)).state shouldBe Attacking
  }
  "A turret" should "filter the interesting entities" in {
    val entities: List[Entity] = List(dummyTurret2, dummyZombie1, dummyZombie2)
    val entitiesFiltered = entities.filter(turret.isInterestedIn)
    assertResult(entitiesFiltered)(List(dummyZombie1))
  }
  "A turret" should "lose HPs after getting hit" in {
    val updatedTurret = turret collideWith dummyBullet
    updatedTurret.life should be < turret.life
  }
  "A turret" should "die if reaches 0 HP" in {
    val updatedTurret = lowHealthTurret collideWith dummyBullet
    updatedTurret.state shouldBe Dead
  }