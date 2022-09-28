package controller.integration

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, ScalaTestWithActorTestKit, TestInbox, TestProbe}
import akka.actor.typed.ActorRef
import controller.Command
import controller.GameController.GameControllerActor
import controller.GameController.GameControllerCommands.{NewEnemiesWave, NewGame, StartGame}
import controller.GameLoop.GameLoopCommands.Start
import controller.WaveController.WaveControllerCommands.GenerateWave
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

class GameControllerIntegrationTest extends AnyWordSpec with Matchers :

  val gameLoop: TestInbox[Command] = TestInbox[Command]()
  val waveController: TestInbox[Command] = TestInbox[Command]()
  val gameController: BehaviorTestKit[Command] = BehaviorTestKit(GameControllerActor(gameLoop.ref, waveController.ref))

  "GameController" when {
    "communicate correctly" should {

      "interact with WaveController" in {
        gameController run NewGame()
        // awful, but only for the moment...first wave and two enemies
        waveController expectMessage GenerateWave(1, 2, gameController.ref)
      }

      "interact with GameLoop" in {
        gameController run NewEnemiesWave(List())
        gameController run StartGame()
        gameLoop expectMessage Start(List())
      }
    }
  }

