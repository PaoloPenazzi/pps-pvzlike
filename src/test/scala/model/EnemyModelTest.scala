package model

import model.entities.{Enemy, Entity, Plant, Turret, WorldSpace, Zombie}
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import WorldSpace.{LanesLength, given}

class EnemyModelTest extends AnyFlatSpec with should.Matchers:
  "A zombie" should "attack a turrets that is in range" in {
    val turret: Turret = Plant(1, LanesLength / 2)
    val zombie: Enemy =  Zombie(1, (LanesLength / 2 + 5) )
    zombie canAttack turret shouldBe true
  }
  "A zombie" should "not attack a turret in another lane" in {
    val turret: Turret = Plant(1, LanesLength / 2)
    val zombie: Enemy = Zombie(2, (LanesLength / 2 + 5))
    zombie canAttack turret shouldBe false
  }


