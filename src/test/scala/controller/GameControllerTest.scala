package controller

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.GameController.GameControllerActor
import controller.GameController.GameControllerCommands.StartGame
import controller.GameLoop.GameLoopActor
import controller.GameLoop.GameLoopCommands.{GameLoopCommand, Start}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameControllerTest extends AnyWordSpec with BeforeAndAfterAll with Matchers :
  val testKit: ActorTestKit = ActorTestKit()
  val controller: BehaviorTestKit[Command] = BehaviorTestKit(GameControllerActor())

  // with the future dsl we have to separate the integration test and behavior test (i.e. t

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "The GameController Actor" when {
    "created" should {
      "be alive" in {
        controller.isAlive must be(true)
      }
    }
  }
