package model

import model.entities.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class TurretModelTest extends AnyFlatSpec with should.Matchers:
  "A turret" should "attack a zombie that is in range" in {
      val turret: Turret = Plant(50, 1)
      val zombie: Enemy = Zombie()
      zombie.position = (100, 1)
      turret canAttack zombie shouldBe true
  }
  "A turret" should "not attack a zombie in another lane" in {
      val turret: Turret = Plant(50, 1)
      val zombie: Enemy = Zombie()
      zombie.position = (100, 2)
      turret canAttack zombie shouldBe false
  }
  "A turret" should "filter the interesting entities" in {
    val turret: Turret = Plant(50, 1)
    val turretInList1: Turret = Plant(100, 2)
    val turretInList2: Turret = Plant(75, 2)
    val zombieInList1: Enemy = Zombie()
    val zombieInList2: Enemy = Zombie()
    zombieInList1.position = (100, 1)
    zombieInList2.position = (100, 2)
    val entities: List[Entity] = List(turretInList1, turretInList2, zombieInList1, zombieInList2)
    assert(entities.filter(turret.filter) == List(zombieInList1))
  }