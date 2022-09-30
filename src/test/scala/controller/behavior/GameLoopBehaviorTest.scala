package controller.behavior

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.Command
import model.entities.{Enemy, Zombie}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldNot
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.FiniteDuration

class GameLoopBehaviorTest extends AnyWordSpec with Matchers :

  import controller.GameLoopActor
  import controller.GameLoopActor.*
  import controller.GameLoopActor.GameLoopCommands.*

  val gameLoopActor: BehaviorTestKit[Command] = BehaviorTestKit(GameLoopActor())
  val enemiesWave: Option[List[Enemy]] = Some(List.fill(3)(Zombie()))


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
        gameLoopActor run PauseLoop()
        gameLoopActor run Start(enemiesWave.get)
        gameLoopActor.returnedBehavior shouldBe Behaviors.same
      }

      "resume the loop" in {
        gameLoopActor run PauseLoop()
        val prevBehavior = gameLoopActor.currentBehavior
        gameLoopActor run ResumeLoop()
        val postBehavior = gameLoopActor.currentBehavior
        prevBehavior should not be postBehavior
      }

      "stop its behavior" in {
        gameLoopActor run Stop()
        gameLoopActor.isAlive must be(false)
      }
    }
  }




