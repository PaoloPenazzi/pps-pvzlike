package controller.integration

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import model.entities.*
import model.common.Utilities.*
import controller.{Command, GameLoopActor, Render, ViewMessage}
import controller.GameLoopActor.*
import controller.GameLoopActor.GameLoopCommands.{EntityUpdated, StartLoop, UpdateLoop}
import model.actors.{Collision, ModelMessage, Update}
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

  var entities: Seq[(ActorRef[ModelMessage], Entity)] = _
  var gameLoopActor: BehaviorTestKit[Command] = _

  before {
    viewActor = TestInbox[ViewMessage]("view")
    seedActor = TestInbox[ModelMessage]("seed")
    zombieActor = TestInbox[ModelMessage]("zombie")
    plantActor = TestInbox[ModelMessage]("plant")

    bullet = (seedActor.ref, PeaBullet(1, LanesLength))
    zombie = (zombieActor.ref, Zombie((1, LanesLength)))
    shooter = (plantActor.ref, PeaShooter(1, LanesLength / 2)())

    entities = List(bullet, zombie, shooter)
    gameLoopActor = BehaviorTestKit(GameLoopActor(viewActor.ref, List(bullet, zombie, shooter)))
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

      "interact with bullet actors" when{
        "find a collisions" in {
          gameLoopActor run UpdateLoop()
          seedActor.receiveMessage()
          seedActor expectMessage Update(Velocity.Normal.speed, List(), gameLoopActor.ref)
          zombieActor.receiveMessage()
          zombieActor expectMessage Update(Velocity.Normal.speed, List(), gameLoopActor.ref)
          plantActor expectMessage Update(Velocity.Normal.speed, List(zombie._2), gameLoopActor.ref)
        }
      }

      "interact with the view" when {
        "an entity is updated" in {
          gameLoopActor run EntityUpdated(seedActor.ref, bullet._2)
          viewActor expectMessage Render(entities.map(_._2).toList, gameLoopActor.ref, MetaData())
        }
      }
      }
    }
