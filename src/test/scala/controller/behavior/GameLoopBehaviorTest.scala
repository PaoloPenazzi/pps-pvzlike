package controller.behavior

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import controller.{Command, ViewActor, ViewMessage}
import model.entities.{Enemy, Zombie}
import org.scalatest.BeforeAndAfterAll
import view.Game
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.shouldNot
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.FiniteDuration

class GameLoopBehaviorTest extends AnyWordSpec with Matchers :

  import controller.GameLoopActor
  import controller.GameLoopActor.*
  import controller.GameLoopActor.GameLoopCommands.*

  val viewActor = TestInbox[ViewMessage]()
  val gameLoopActor: BehaviorTestKit[Command] = BehaviorTestKit(GameLoopActor(viewActor.ref))
  val enemiesWave: Option[List[Enemy]] = Some(List.fill(3)(Zombie()))


  "The GameLoop Actor" when {
    "created" should {
      "be alive" in {
        gameLoopActor.isAlive must be(true)
      }

      "resume the loop" in {
        gameLoopActor run PauseLoop()
        val prevBehavior = gameLoopActor.currentBehavior
        gameLoopActor run ResumeLoop()
        val postBehavior = gameLoopActor.currentBehavior
        prevBehavior should not be postBehavior
      }
    }
  }




