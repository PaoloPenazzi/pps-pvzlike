package model

import model.entities.{Enemy, Plant, Turret}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class EnemyModelTest extends AnyFlatSpec with should.Matchers:
  "A zombie" should "attack a turrets that is in range" in {
    val turret: Turret = Plant(50, 1)
    val zombie: Enemy =  Zombie()
    zombie.position = (100, 1)
    turret canAttack zombie shouldBe true
  }
  "A turret" should "not attack a zombie in another lane" in {
    val turret: Turret = Plant(50, 1)
    val zombie: Enemy = Zombie()
    zombie.position = (100, 2)
    turret canAttack zombie shouldBe false
  }
