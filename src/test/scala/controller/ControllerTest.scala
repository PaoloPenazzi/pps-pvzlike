package controller

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.Controller.ControllerActor
import controller.Controller.ControllerCommands.StartGame
import controller.GameLoop.GameLoopActor
import controller.GameLoop.GameLoopCommands.{GameLoopCommand, Start}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ControllerTest extends AnyWordSpec with BeforeAndAfterAll with Matchers :
  val testKit = ActorTestKit()
  val controller = BehaviorTestKit(ControllerActor())


  override def afterAll(): Unit = testKit.shutdownTestKit()

  "The Controller Actor" when {
    "created" should {
      "be alive" in {
        controller.isAlive must be(true)
      }

      /*      "spawn a new game loop" in {
        controller run StartGame()
        val childBox = controller.childInbox[GameLoopCommand]("gameloop")
        childBox.expectMessage(Start())
        controller expectEffect Effect.Spawned(GameLoopActor(), "gameloop")
        }
    }*/
    }
  }
