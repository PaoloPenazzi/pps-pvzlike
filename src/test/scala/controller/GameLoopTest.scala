package controller

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import org.scalatest.funsuite.AnyFunSuite

class GameLoopTest extends AnyFunSuite:

  import controller.GameLoop.*

  test("Test GameLoop Actor Spawning") {
    val gameLoopActor = BehaviorTestKit(GameLoopActor())
    gameLoopActor.isAlive
  }

