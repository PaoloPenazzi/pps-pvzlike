import org.scalatest.*
import flatspec.*
import matchers.*
import model.entities.{Plant, Zombie, Enemy, Turret}

class TurretModelTest extends AnyFlatSpec with should.Matchers:
  "A turret" should "see a zombie in range" in {
    val turret: Turret = Plant(50, 1)
    val zombie: Enemy = Zombie()
    zombie.position = (100, 1)
    turret canAttack zombie shouldBe true
  }
