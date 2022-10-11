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

class TroopActorTest extends AnyWordSpec with BeforeAndAfterAll with Matchers :

  val plant: Turret = Plant((1, LanesLength))
  val zombie: Zombie = Zombie((1, LanesLength + 2))
  val turretTroopActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(plant))
  val zombieTroopActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(zombie))

  "The troop actor" when {
    "created" should {
      "be alive" in {
        turretTroopActor.isAlive must be(true)
      }
    }
  }

  "The troop actor (turret)" when {
    "updated" should {
      "attack the zombie" in {
        val inbox = TestInbox[Command]()
        turretTroopActor run Update(FiniteDuration(32, MILLISECONDS), List(zombie), inbox.ref)
        turretTroopActor expectEffect Effect.TimerScheduled("Shooting",
          Shoot(inbox.ref),
          plant.fireRate.seconds,
          Effect.TimerScheduled.SingleMode, false)(null)
      }
      "update his position" in {
        val inbox = TestInbox[Command]()
        turretTroopActor run Update(FiniteDuration(32, MILLISECONDS), List(zombie), inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityUpdated[Entity]])
      }
    }
    "shooting" should {
      "spawn a bullet" in {
        val inbox = TestInbox[Command]()
        turretTroopActor run Shoot(inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntitySpawned[Bullet]])
      }
    }
  }


  "The troop actor (zombie)" when {
    "is updated" should {
      "attack the plant" in {
        val inbox = TestInbox[Command]()
        zombieTroopActor run Update(FiniteDuration(32, MILLISECONDS), List(plant), inbox.ref)
        zombieTroopActor expectEffect Effect.TimerScheduled("Shooting",
          Shoot(inbox.ref),
          zombie.fireRate.seconds,
          Effect.TimerScheduled.SingleMode, false)(null)
      }
      "update his position" in {
        val inbox = TestInbox[Command]()
        zombieTroopActor run Update(FiniteDuration(32, MILLISECONDS), List(plant), inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityUpdated[Entity]])
      }
    }
    "shooting" should {
      "spawn a bullet" in {
        val inbox = TestInbox[Command]()
        zombieTroopActor run Shoot(inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntitySpawned[Bullet]])
      }
    }
  }

