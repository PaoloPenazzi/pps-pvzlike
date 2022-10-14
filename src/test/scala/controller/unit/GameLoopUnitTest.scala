package controller.unit

import akka.actor.testkit.typed.scaladsl.{ScalaTestWithActorTestKit, TestInbox}
import controller.GameLoopActor.GameLoopActor
import controller.{Command, ViewMessage}
import model.entities.{Enemy, Entity, Zombie}
import model.entities.WorldSpace.LanesLength
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}
import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import model.actors.ModelMessage
import model.entities.*


class GameLoopUnitTest extends AnyWordSpec with Matchers:

  val viewActor: TestInbox[ViewMessage] = TestInbox[ViewMessage]("view")
  val seedActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("seed")
  val zombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("zombie")
  val plantActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("plant")
  val seed: (ActorRef[ModelMessage], Seed) = (seedActor.ref, Seed(1,LanesLength))
  val zombie: (ActorRef[ModelMessage], Zombie) = (zombieActor.ref, Zombie((1, LanesLength)))
  val plant: (ActorRef[ModelMessage], Plant) = (plantActor.ref, Plant(1, LanesLength / 2)())


  "The game loop" when {
    "created" should {
      
      "calculate the collision" in {
        val gameLoop: GameLoopActor = controller.GameLoopActor.GameLoopActor(viewActor.ref, List(seed, zombie))
        gameLoop.detectCollision shouldEqual List((seed, zombie))
      }
     
      "calculate the interests" in {
        val gameLoop: GameLoopActor = controller.GameLoopActor.GameLoopActor(viewActor.ref, List(zombie, plant))
        gameLoop.detectInterest shouldEqual List((zombieActor.ref, List()), (plantActor.ref, List(zombie._2) ))
      }
      
      "calculate if the wave is over" in {
        val gameLoop: GameLoopActor = controller.GameLoopActor.GameLoopActor(viewActor.ref, List())
        gameLoop.isWaveOver should be(true)
      }
    }

  }

