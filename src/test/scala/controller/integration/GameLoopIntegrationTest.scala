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

  case class MockSystem(viewActor: TestInbox[ViewMessage] = TestInbox[ViewMessage]("view"),
                        seedActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("seed"),
                        zombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("zombie"),
                        plantActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("plant")):
    val bullet: (ActorRef[ModelMessage], PeaBullet) = (seedActor.ref, PeaBullet((1, LanesLength)))
    val zombie: (ActorRef[ModelMessage], BasicZombie) = (zombieActor.ref, BasicZombie((1, LanesLength)))
    val shooter: (ActorRef[ModelMessage], PeaShooter) = (plantActor.ref, PeaShooter((1, LanesLength / 2)))
    val entities: Seq[(ActorRef[ModelMessage], Entity)] = List(bullet, zombie, shooter)
    val gameLoopActor: BehaviorTestKit[Command] = BehaviorTestKit(GameLoopActor(viewActor.ref, List(bullet, zombie, shooter)))


  "GameController" when {
    "communicate correctly" should {

      "interact with actors" when {
        "updating" in {
          val mockSystem = MockSystem()
          mockSystem.gameLoopActor run UpdateLoop()
          mockSystem.seedActor expectMessage Collision(mockSystem.zombie._2, mockSystem.gameLoopActor.ref)
          mockSystem.zombieActor expectMessage Collision(mockSystem.bullet._2, mockSystem.gameLoopActor.ref)
        }
      }

      "interact with bullet actors" when{
        "find a collisions" in {
          val mockSystem = MockSystem()
          mockSystem.gameLoopActor run UpdateLoop()
          mockSystem.seedActor.receiveMessage()
          mockSystem.seedActor expectMessage Update(Velocity.Normal.speed, List(), mockSystem.gameLoopActor.ref)
          mockSystem.zombieActor.receiveMessage()
          mockSystem.zombieActor expectMessage Update(Velocity.Normal.speed, List(), mockSystem.gameLoopActor.ref)
          mockSystem.plantActor expectMessage Update(Velocity.Normal.speed, List(mockSystem.zombie._2), mockSystem.gameLoopActor.ref)
        }
      }

      "interact with the view" when {
        "an entity is updated" in {
          val mockSystem = MockSystem()
          mockSystem.gameLoopActor run EntityUpdated(mockSystem.seedActor.ref, mockSystem.bullet._2)
          mockSystem.viewActor expectMessage Render(mockSystem.entities.map(_._2).toList, mockSystem.gameLoopActor.ref, MetaData())
        }
      }
      }
    }
