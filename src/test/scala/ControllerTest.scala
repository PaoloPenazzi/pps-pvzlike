import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.scalatest.funsuite.AnyFunSuite

class ControllerTest extends AnyFunSuite:
  import controller.Controller.*

  test("Test Controller Actor Spawning") {
    val controllerActorTest = BehaviorTestKit(ControllerActor())
    controllerActorTest.isAlive
  }

