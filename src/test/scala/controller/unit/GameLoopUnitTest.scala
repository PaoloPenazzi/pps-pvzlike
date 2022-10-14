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

class GameLoopUnitTest extends AnyWordSpec with Matchers:

  val viewActor: TestInbox[ViewMessage] = TestInbox[ViewMessage]()
  val gameLoop: GameLoopActor = controller.GameLoopActor.GameLoopActor(viewActor.ref)

  "The game loop" when {
    "created" should {
      
      "calculate the collision" in {
        
      }
     
      "calculate the interests" in {
        
      } 
    }

  }

