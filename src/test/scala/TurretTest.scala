import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

/*class TurretModelTest extends AnyFunSuite:
  test("An new turret is created correctly") {
    val turret = Turret(50, 50)
    assert(turret.hp == 100)
    assert(turret.position == (50, 50))
    assert(turret.fireRate == 1)
    assert(!turret.isShooting)
  }*/

