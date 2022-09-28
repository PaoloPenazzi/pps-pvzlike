package controller

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.FiniteDuration

class GameLoopTest extends AnyWordSpec with BeforeAndAfterAll with Matchers :

  import controller.GameLoop.*
  import controller.GameLoop.GameLoopCommands.*

  val testKit = ActorTestKit()
  val gameLoopActor = BehaviorTestKit(GameLoopActor())

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "The GameLoop Actor" when {
    "created" should {
      "be alive" in {
        gameLoopActor.isAlive must be(true)
      }

      "start a new game" in {
        gameLoopActor run Start()
        gameLoopActor expectEffect Effect.TimerScheduled(Update(), Update(), FiniteDuration(10, "second"), Effect.TimerScheduled.SingleMode, false)(null)
      }

      "stop its behavior" in {
        gameLoopActor run Stop()
        gameLoopActor.isAlive must be(false)
      }
    }
  }




