package model.actors

import akka.actor.ActorSystem
import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, ScalaTestWithActorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import controller.Command
import controller.GameLoopActor.GameLoopCommands.{EntitySpawned, EntityUpdated, GameLoopCommand}
import model.actors.{BulletActor, Shoot, TurretActor, Update}
import model.common.DefaultValues.*
import model.entities.*
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}
import scala.language.implicitConversions

import scala.concurrent.duration.{DurationInt, FiniteDuration, MILLISECONDS}
import WorldSpace.LanesLength

class TroopActorTest extends AnyWordSpec with BeforeAndAfterAll with Matchers:

  val plant: Turret = Plant((1, LanesLength / 2))
  val zombie: Zombie = Zombie((1, LanesLength))
  val firstTroopActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(plant))
  val secondTroopActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(zombie))
  val inbox = TestInbox[Command]()

  "The troop actor" when {
    "created" should {
      "be alive in" {
        firstTroopActor.isAlive must be(true)
      }
    }
  }

