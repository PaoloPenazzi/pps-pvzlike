package model

import model.entities.WorldSpace.LanesLength
import model.entities.{Enemy, Entity, Plant, Turret, WorldSpace, Zombie}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class EnemyModelTest extends AnyFlatSpec with should.Matchers:
  "A zombie" should "attack a turrets that is in range" in {
    val turret: Turret = Plant(50, 1)
    val zombie: Enemy =  Zombie(100, 1)
    zombie canAttack turret shouldBe true
  }
  "A turret" should "not attack a zombie in another lane" in {
    val turret: Turret = Plant(50, 1)
    val zombie: Enemy = Zombie(100, 2)
    zombie canAttack turret shouldBe false
  }


