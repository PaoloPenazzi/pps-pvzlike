package controller.integration

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import model.entities.*
import model.common.Utilities.*
import controller.{Command, GameLoopActor, Render, ViewMessage}
import controller.GameLoopActor.*
import controller.GameLoopActor.GameLoopCommands.{EntityUpdated, StartLoop, UpdateLoop}
import model.GameData.{GameEntity, GameSeq}
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

  var bullet: GameEntity[Entity] = _
  var zombie: GameEntity[Entity] = _
  var shooter: GameEntity[Entity] = _

  var entities: GameSeq = _
  var gameLoopActor: BehaviorTestKit[Command] = _

  before {
    viewActor = TestInbox[ViewMessage]("view")
    seedActor = TestInbox[ModelMessage]("seed")
    zombieActor = TestInbox[ModelMessage]("zombie")
    plantActor = TestInbox[ModelMessage]("plant")

    bullet = GameEntity(seedActor.ref, PeaBullet((1, LanesLength)))
    zombie = GameEntity(zombieActor.ref, BasicZombie((1, LanesLength)))
    shooter = GameEntity(plantActor.ref, PeaShooter((1, LanesLength / 2)))

    entities = GameSeq(List(bullet, zombie, shooter))
    gameLoopActor = BehaviorTestKit(GameLoopActor(viewActor.ref, entities))
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
          viewActor expectMessage Render(entities.seq.map(_.entity).toList, gameLoopActor.ref, MetaData())
        }
      }
      }
    }
