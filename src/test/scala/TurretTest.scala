import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import Turrets.*
import DefaultValues.*

class TurretModelTest extends AnyFunSuite:
  test("Test default plant values") {
    val testPosition: (Int, Int) = (0,0)
    val testPlantTurret = new Plant(testPosition)
    assert(testPlantTurret.cost == 100)
    assert(testPlantTurret.fireRate == 1.0)
  }
  
  test("Test default zombie values") {
    val testPosition: (Int, Int) = (0,0)
    val testZombie = new Zombie(testPosition)
    assert(testZombie.fireRate == 0.8)
  }

