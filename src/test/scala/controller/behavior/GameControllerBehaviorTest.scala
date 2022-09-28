package controller.behavior

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.Command
import controller.GameController.GameControllerActor
import controller.GameController.GameControllerCommands.StartGame
import controller.GameLoop.GameLoopActor
import controller.GameLoop.GameLoopCommands.{GameLoopCommand, Start}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameControllerBehaviorTest extends AnyWordSpec with Matchers :
  
  val controller: BehaviorTestKit[Command] = BehaviorTestKit(GameControllerActor(TestInbox[Command]().ref, TestInbox[Command]().ref))


  "The GameController Actor" when {
    "created" should {
      "be alive" in {
        controller.isAlive must be(true)
      }
    }
  }
