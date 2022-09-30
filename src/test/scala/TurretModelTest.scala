import org.scalatest.*
import flatspec.*
import matchers.*
import model.entities.{Plant, Zombie, Enemy, Turret}

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


