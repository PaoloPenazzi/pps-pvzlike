package controller.integration

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import controller.{Command, Render, ViewMessage}
import controller.GameLoopActor.{GameLoopActor, updateTime}
import controller.GameLoopActor.GameLoopCommands.{EntityUpdated, UpdateLoop}
import model.actors.{Collision, ModelMessage, Update}
import model.entities.*
import model.entities.WorldSpace.LanesLength
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameLoopIntegrationTest extends AnyWordSpec with BeforeAndAfter with Matchers :

  var viewActor: TestInbox[ViewMessage] = _
  var seedActor: TestInbox[ModelMessage] = _
  var zombieActor: TestInbox[ModelMessage] = _
  var plantActor: TestInbox[ModelMessage] = _

  var seed: (ActorRef[ModelMessage], Seed) = _
  var zombie: (ActorRef[ModelMessage], Zombie) = _
  var plant: (ActorRef[ModelMessage], Plant) = _

  var gameLoop: GameLoopActor = _
  var gameLoopActor: BehaviorTestKit[Command] = _

  before {
    viewActor = TestInbox[ViewMessage]("view")
    seedActor = TestInbox[ModelMessage]("seed")
    zombieActor = TestInbox[ModelMessage]("zombie")
    plantActor = TestInbox[ModelMessage]("plant")

    seed = (seedActor.ref, Seed(1, LanesLength))
    zombie = (zombieActor.ref, Zombie((1, LanesLength)))
    plant = (plantActor.ref, Plant(1, LanesLength / 2)())

    gameLoop = controller.GameLoopActor.GameLoopActor(viewActor.ref, List(seed, zombie, plant))
    gameLoopActor = BehaviorTestKit(gameLoop.standardBehavior)
  }

  "GameController" when {
    "communicate correctly" should {

      "interact with actors" when {
        "updating" in {
          gameLoopActor run UpdateLoop()
          seedActor expectMessage Collision(zombie._2, gameLoopActor.ref)
          zombieActor expectMessage Collision(seed._2, gameLoopActor.ref)
        }
      }

      "interact with bullet actors" when{
        "find a collisions" in {
          gameLoopActor run UpdateLoop()
          seedActor.receiveMessage()
          seedActor expectMessage Update(updateTime, List(), gameLoopActor.ref)
          zombieActor.receiveMessage()
          zombieActor expectMessage Update(updateTime, List(), gameLoopActor.ref)
          plantActor expectMessage Update(updateTime, List(zombie._2), gameLoopActor.ref)
        }
      }

      "interact with the view" when {
        "an entity is updated" in {
          gameLoopActor run EntityUpdated(seedActor.ref, seed._2)
          viewActor expectMessage Render(gameLoop.entities.map(_._2).toList, gameLoopActor.ref)
        }
      }
      }
    }
