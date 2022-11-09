package controller.integration

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.{ActorRef, Behavior}
import model.entities.*
import model.common.Utilities.*
import controller.{GameLoopActor, RenderEntities, RenderMetaData, ViewMessage}
import controller.GameLoopActor.*
import controller.GameLoopActor.GameLoopCommands.{Command, EntityUpdated, StartGame, UpdateLoop, UpdateResources}
import model.GameData.{GameEntity, GameSeq}
import model.actors.{Collision, ModelMessage, Update}
import model.entities.WorldSpace.LanesLength
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.implicitConversions

class GameLoopIntegrationTest extends AnyWordSpec with BeforeAndAfter with Matchers :

  case class MockSystem(viewActor: TestInbox[ViewMessage] = TestInbox[ViewMessage]("view"),
                        seedActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("seed"),
                        zombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("zombie"),
                        plantActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("plant")):

    val bullet: GameEntity[Entity] = GameEntity(seedActor.ref, Bullets.ofType[PeaBullet] withPosition (1, LanesLength))
    val zombie: GameEntity[Entity] = GameEntity(zombieActor.ref, Troops.ofType[BasicZombie] withPosition (1, LanesLength))
    val shooter: GameEntity[Entity] = GameEntity(plantActor.ref, Troops.shooterOf[PeaBullet] withPosition (1, LanesLength / 2))
    val entities: Seq[GameEntity[Entity]] = List(bullet, zombie, shooter)
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
          mockSystem.seedActor expectMessage Update(Speed.Normal.gameSpeed, List(), mockSystem.gameLoopActor.ref)
          mockSystem.zombieActor.receiveMessage()
          mockSystem.zombieActor expectMessage Update(Speed.Normal.gameSpeed, List(), mockSystem.gameLoopActor.ref)
          mockSystem.plantActor expectMessage Update(Speed.Normal.gameSpeed, List(mockSystem.zombie._2), mockSystem.gameLoopActor.ref)
        }
      }

      "interact with the view" when {
        "an entity is updated" in {
          val mockSystem = MockSystem()
          mockSystem.gameLoopActor run EntityUpdated(mockSystem.seedActor.ref, mockSystem.bullet._2)
          mockSystem.viewActor expectMessage RenderEntities(mockSystem.entities.map(_._2).toList, mockSystem.gameLoopActor.ref)
        }
        "a resource is updated" in {
          val mockSystem = MockSystem()
          mockSystem.gameLoopActor run UpdateResources()
          mockSystem.viewActor expectMessage RenderMetaData(MetaData(50), mockSystem.gameLoopActor.ref)
        }
      }


      }
    }
