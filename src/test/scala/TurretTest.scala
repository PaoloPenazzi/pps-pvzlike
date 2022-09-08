import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import Turret.*
import akka.actor.typed.ActorRef
import TurretActor.*
import TurretActorTest._

class TurretModelTest extends AnyFunSuite:
  test("An new turret is created correctly") {
    val turret = Turret(50, 50)
    assert(turret.hp == 100)
    assert(turret.position == (50, 50))
    assert(turret.fireRate == 1)
    assert(!turret.isShooting)
  }

object TurretActorTest:
  def waitSomeTime(): Unit = Thread.sleep(4000)
  val testTurret: Turret = Turret(0, 0)

class TurretActorTest extends ScalaTestWithActorTestKit with AnyWordSpecLike:
  val turretActor: ActorRef[TurretMessages] = testKit.spawn(TurretActor(testTurret))

  "The TurretActor" when {
    "the wave isn't coming" should {
      "not shoot" in {
        turretActor ! waveFinished()
        waitSomeTime()
        testTurret.isShooting shouldBe false
      }
    }
    "the wave is coming" should {
      "shoot" in {
        turretActor ! waveStarted()
        waitSomeTime()
        testTurret.isShooting shouldBe true
      }
    }
  }

