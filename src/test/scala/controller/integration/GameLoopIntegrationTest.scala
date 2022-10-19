package controller.integration

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import model.entities.*
import controller.{Command, Render, ViewMessage}
import controller.GameLoopActor.{GameLoopActor, updateTime}
import controller.GameLoopActor.GameLoopCommands.{EntityUpdated, StartLoop, StartResourcesLoop, UpdateLoop}
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

  var bullet: (ActorRef[ModelMessage], PeaBullet) = _
  var zombie: (ActorRef[ModelMessage], Zombie) = _
  var shooter: (ActorRef[ModelMessage], PeaShooter) = _

  var gameLoop: GameLoopActor = _
  var gameLoopActor: BehaviorTestKit[Command] = _

  before {
    viewActor = TestInbox[ViewMessage]("view")
    seedActor = TestInbox[ModelMessage]("seed")
    zombieActor = TestInbox[ModelMessage]("zombie")
    plantActor = TestInbox[ModelMessage]("plant")

    bullet = (seedActor.ref, PeaBullet(1, LanesLength))
    zombie = (zombieActor.ref, Zombie((1, LanesLength)))
    shooter = (plantActor.ref, PeaShooter(1, LanesLength / 2)())

    gameLoop = controller.GameLoopActor.GameLoopActor(viewActor.ref, List(bullet, zombie, shooter))
    gameLoopActor = BehaviorTestKit(gameLoop.standardBehavior)
  }

  "GameController" when {
    "communicate correctly" should {

      "interact with actors" when {
        "updating" in {
          gameLoopActor run UpdateLoop()
          seedActor expectMessage Collision(zombie._2, gameLoopActor.ref)
          zombieActor expectMessage Collision(bullet._2, gameLoopActor.ref)
        }
      }

      "interact with himself" when {
        "start the resource timer" in {
          gameLoopActor run StartLoop()
          gameLoopActor.selfInbox() expectMessage  StartResourcesLoop()
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
          gameLoopActor run EntityUpdated(seedActor.ref, bullet._2)
          viewActor expectMessage Render(gameLoop.entities.map(_._2).toList, gameLoopActor.ref, MetaData())
        }
      }
      }
    }
