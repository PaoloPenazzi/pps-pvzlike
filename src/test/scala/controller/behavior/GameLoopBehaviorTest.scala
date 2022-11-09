package controller.behavior

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import controller.actors.GameLoopActor
import controller.actors.GameLoopActor.*
import controller.actors.GameLoopActor.GameLoopCommands.*
import controller.{ViewMessage, actors}
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
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, BeforeAndAfterEach}
import view.Game

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps

class GameLoopBehaviorTest extends AnyWordSpec with BeforeAndAfter with Matchers :

  var mockSystem: MockSystem = MockSystem()

  def resourcesTime: FiniteDuration = FiniteDuration(3, "seconds")

  def updateTime: FiniteDuration = Speed.Normal.gameSpeed

  before {
    mockSystem = MockSystem()
  }

  case class MockSystem():
    val viewActor: TestInbox[ViewMessage] = TestInbox[ViewMessage]()
    val gameLoopActor: BehaviorTestKit[Command] = BehaviorTestKit(actors.GameLoopActor(viewActor.ref))

    def send(msg: Command): Unit = gameLoopActor run msg

    def isAlive: Boolean = gameLoopActor.isAlive

    def getBehavior: Behavior[Command] = gameLoopActor.currentBehavior

    def getEffect: Effect = gameLoopActor.retrieveEffect()

    def expectTimerEffect(msg: Command, updateTime: FiniteDuration): Unit =
      expectEffect(Effect.TimerScheduled(msg, msg, updateTime, Effect.TimerScheduled.SingleMode, false)(null))

    def expectEffect(effect: Effect): Unit = gameLoopActor.expectEffect(effect)

  "The GameLoop Actor" when {
    "created" should {
      "be alive" in {
        mockSystem.isAlive must be(true)
      }

      "create a wave" in {
        mockSystem send StartGame()
        val startTimerEffect = mockSystem.getEffect
        val startWaveEffect = mockSystem.getEffect
        startTimerEffect should not be startWaveEffect
      }

      "start the loop timer" in {
        mockSystem send StartGame()
        mockSystem expectTimerEffect(UpdateLoop(), updateTime)
      }

      "start the resources timer" in {
        mockSystem send StartGame()
        mockSystem.getEffect
        mockSystem expectTimerEffect(UpdateResources(), resourcesTime)
      }

      "resume the loop" in {
        mockSystem send PauseGame()
        val prevBehavior = mockSystem.getBehavior
        mockSystem send ResumeGame()
        val postBehavior = mockSystem.getBehavior
        prevBehavior should not be postBehavior
      }
    }
  }




