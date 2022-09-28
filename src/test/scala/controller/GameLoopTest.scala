package controller

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import model.entities.{Enemy, Zombie}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers.shouldNot
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.FiniteDuration

class GameLoopTest extends AnyWordSpec with BeforeAndAfterAll with Matchers :

  import controller.GameLoop.*
  import controller.GameLoop.GameLoopCommands.*

  // make a dsl to simplify and avoid repetion with akka testkit

  val testKit: ActorTestKit = ActorTestKit()
  val gameLoopActor: BehaviorTestKit[Command] = BehaviorTestKit(GameLoopActor())
  val enemiesWave: Option[List[Enemy]] = Some(List.fill(3)(Zombie()))

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "The GameLoop Actor" when {
    "created" should {
      "be alive" in {
        gameLoopActor.isAlive must be(true)
      }

      "start a new timer" in {
        gameLoopActor run Start(enemiesWave.get)
        gameLoopActor expectEffect Effect.TimerScheduled(Update(), Update(), FiniteDuration(10, "second"), Effect.TimerScheduled.SingleMode, false)(null)
      }

      "pause the loop" in {
        gameLoopActor run Pause()
        gameLoopActor run Start(enemiesWave.get)
        gameLoopActor.returnedBehavior shouldBe Behaviors.same
      }

      "resume the loop" in {
        gameLoopActor run Pause()
        val prevBehavior = gameLoopActor.currentBehavior
        gameLoopActor run Resume()
        val postBehavior = gameLoopActor.currentBehavior
        prevBehavior should not be postBehavior
      }

      "stop its behavior" in {
        gameLoopActor run Stop()
        gameLoopActor.isAlive must be(false)
      }
    }
  }




