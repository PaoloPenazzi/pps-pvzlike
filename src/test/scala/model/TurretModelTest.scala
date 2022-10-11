package model

import model.entities.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import scala.language.implicitConversions
import WorldSpace.{LanesLength, given}

class TurretModelTest extends AnyFlatSpec with should.Matchers:
  "A turret" should "attack a zombie that is in range" in {
      val turret: Turret = Plant((1, LanesLength / 2))
      val zombie: Enemy = Zombie((1, LanesLength))
      turret canAttack zombie shouldBe true
  }
  "A turret" should "not attack a zombie in another lane" in {
      val turret: Turret = Plant((1, LanesLength / 2))
      val zombie: Enemy = Zombie((2, LanesLength))
      turret canAttack zombie shouldBe false
  }
  "A turret" should "filter the interesting entities" in {
    val turret: Turret = Plant((1, LanesLength / 2))
    val turretInList1: Turret = Plant((2, LanesLength))
    val turretInList2: Turret = Plant((2, LanesLength * 0.75))
    val zombieInList1: Enemy = Zombie((1, LanesLength))
    val zombieInList2: Enemy = Zombie((2, LanesLength))
    val entities: List[Entity] = List(turretInList1, turretInList2, zombieInList1, zombieInList2)
    assert(entities.filter(turret.interest) == List(zombieInList1))
  }