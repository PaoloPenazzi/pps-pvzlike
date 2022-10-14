package controller.integration

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import controller.{Command, ViewMessage}
import controller.GameLoopActor.GameLoopActor
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameLoopIntegrationTest extends AnyWordSpec with Matchers :

  val view: TestInbox[ViewMessage] = TestInbox[ViewMessage]()
  val gameLoop: TestInbox[Command] = TestInbox[Command]()
  val waveController: TestInbox[Command] = TestInbox[Command]()

  "GameController" when {
    "communicate correctly" should {

      "interact with actors when updating" in {
        val gameLoop: BehaviorTestKit[Command] = BehaviorTestKit(GameLoopActor(view.ref, List()).standardBehavior)
      }

      "interact with bullet actors when find a collisions" in {

      }

      "interact with WaveController" in {
        // gameController run NewGame()
        // awful, but only for the moment...first wave and two enemies
        // waveController expectMessage GenerateWave(1, 2, gameController.ref)
      }

      "interact with GameLoop" in {
//        gameController run NewEnemiesWave(List())
//        gameController run StartGame()
//        gameLoop expectMessage Start(List())
      }
    }
  }
