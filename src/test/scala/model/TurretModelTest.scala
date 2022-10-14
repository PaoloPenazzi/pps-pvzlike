package model

import model.entities.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import scala.language.implicitConversions
import WorldSpace.{LanesLength, given}

class TurretModelTest extends AnyFlatSpec with should.Matchers:
  val testTurret: Turret = Plant(1, LanesLength / 2)()
  val lowHealthTurret: Turret = Plant(1, LanesLength)(25)
  val dummyTurret1: Turret = Plant(2, LanesLength)()
  val dummyZombie1: Enemy = Zombie((1, LanesLength))
  val dummyZombie2: Enemy = Zombie((2, LanesLength))
  val dummyBullet: Bullet = PeaBullet(0,0)

  "A turret" should "attack a zombie that is in range" in {
      testTurret canAttack dummyZombie1 shouldBe true
  }
  "A turret" should "filter the interesting entities" in {
    val entities: List[Entity] = List(dummyTurret1, dummyZombie1, dummyZombie2)
    val entitiesFiltered = entities.filter(testTurret.isInterestedIn)
    assertResult(entitiesFiltered)(List(dummyZombie1))
  }
  "A turret" should "lose HPs after getting hitted" in {
    val updatedTurret = testTurret collideWith dummyBullet
    assert(updatedTurret.get.life < testTurret.life)
  }
  "A turret" should "die if reaches 0 HP" in {
    val updatedTurret = lowHealthTurret collideWith dummyBullet
    assert(updatedTurret.isEmpty)
  }