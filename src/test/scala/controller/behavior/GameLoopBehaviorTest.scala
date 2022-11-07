package controller.behavior

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.GameLoopActor.*
import controller.GameLoopActor.GameLoopCommands.*
import controller.{GameLoopActor, ViewMessage}
import model.actors.ModelMessage
import model.common.Utilities.Speed
import model.entities.*
import model.entities.WorldSpace.{LanesLength, given}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldNot
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import view.Game

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

class GameLoopBehaviorTest extends AnyWordSpec with BeforeAndAfter with Matchers :

  case class MockSystem(viewActor: TestInbox[ViewMessage] = TestInbox[ViewMessage](),
                        updateTime: FiniteDuration = Speed.Normal.speed,
                        resourcesTime: FiniteDuration = FiniteDuration(3, "seconds")):
    val gameLoopActor: BehaviorTestKit[Command] = BehaviorTestKit(GameLoopActor(viewActor.ref))


  "The GameLoop Actor" when {
    "created" should {
      "be alive" in {
        val mockSystem = MockSystem()
        mockSystem.gameLoopActor.isAlive must be(true)
      }

      "create a wave" in {
        val mockSystem = MockSystem()
        mockSystem.gameLoopActor run StartGame()
        val startTimerEffect = mockSystem.gameLoopActor.retrieveEffect()
        val startWaveEffect = mockSystem.gameLoopActor.retrieveEffect()
        startTimerEffect should not be Effect.NoEffects
        startWaveEffect should not be Effect.NoEffects
        startTimerEffect should not be startWaveEffect
      }

      "start the loop timer" in {
        val mockSystem = MockSystem()
        mockSystem.gameLoopActor run StartGame()
        mockSystem.gameLoopActor expectEffect Effect.TimerScheduled(UpdateLoop(), UpdateLoop(), mockSystem.updateTime, Effect.TimerScheduled.SingleMode, false)(null)
      }

      "start the resources timer" in {
        val mockSystem = MockSystem()
        mockSystem.gameLoopActor run StartGame()
        mockSystem.gameLoopActor.retrieveEffect()
        mockSystem.gameLoopActor expectEffect Effect.TimerScheduled(UpdateResources(), UpdateResources(), mockSystem.resourcesTime, Effect.TimerScheduled.SingleMode, false)(null)
      }

      "resume the loop" in {
        val mockSystem = MockSystem()
        mockSystem.gameLoopActor run PauseGame()
        val prevBehavior = mockSystem.gameLoopActor.currentBehavior
        mockSystem.gameLoopActor run ResumeGame()
        val postBehavior = mockSystem.gameLoopActor.currentBehavior
        prevBehavior should not be postBehavior
      }
    }
  }




