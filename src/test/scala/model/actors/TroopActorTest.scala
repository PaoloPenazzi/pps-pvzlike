package model.actors

import akka.actor.ActorSystem
import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, ScalaTestWithActorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import controller.actors.GameLoopActor.GameLoopCommands.{BulletSpawned, Command, EntityDead, EntityUpdated}
import model.actors.{BulletActor, Shoot, Update}
import model.entities.*
import model.entities.WorldSpace.LanesLength
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

import scala.concurrent.duration.{DurationInt, FiniteDuration, MILLISECONDS}
import scala.language.implicitConversions

class TroopActorTest extends AnyWordSpec with BeforeAndAfterAll with Matchers :

  val plant: Troop = Troops.shooterOf[PeaBullet] withPosition(1, 10)
  val zombie: Troop = BasicZombie((1, LanesLength + 2))
  val plantActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(plant))
  val zombieActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(zombie))
  val lowHealthPlantActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(plant withLife 15))

  "The troop actor" when {
    "created" should {
      "be alive" in {
        plantActor.isAlive must be(true)
      }
    }
  }
  "The troop actor (turret)" when {
    "updated" should {
      "attack the zombie" in {
        val inbox = TestInbox[Command]()
        plantActor run Update(FiniteDuration(32, MILLISECONDS), List(zombie), inbox.ref)
        plantActor expectEffect Effect.TimerScheduled("Shooting",
          Shoot(inbox.ref),
          plant.fireRate.seconds,
          Effect.TimerScheduled.SingleMode, false)(null)
      }
      "update his position" in {
        val inbox = TestInbox[Command]()
        plantActor run Update(FiniteDuration(32, MILLISECONDS), List(zombie), inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityUpdated[Entity]])
      }
    }
    "shooting" should {
      "spawn a bullet" in {
        val inbox = TestInbox[Command]()
        plantActor run Shoot(inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[BulletSpawned])
      }
    }
    "colliding with a bullet" should {
      "update himself" in {
        val inbox = TestInbox[Command]()
        plantActor run Collision(PeaBullet(1, 1), inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityUpdated[Entity]])
      }
    }
    "colliding with a bullet" should {
      "die if has 0 HPs" in {
        val inbox = TestInbox[Command]()
        lowHealthPlantActor run Collision(PeaBullet(1, 1), inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityDead[Entity]])
      }
    }
  }


  "The troop actor (zombie)" when {
    "is updated" should {
      "attack the plant" in {
        val inbox = TestInbox[Command]()
        zombieActor run Update(FiniteDuration(32, MILLISECONDS), List(plant), inbox.ref)
        zombieActor expectEffect Effect.TimerScheduled("Shooting",
          Shoot(inbox.ref),
          zombie.fireRate.seconds,
          Effect.TimerScheduled.SingleMode, false)(null)
      }
      "update his position" in {
        val inbox = TestInbox[Command]()
        zombieActor run Update(FiniteDuration(32, MILLISECONDS), List(plant), inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityUpdated[Entity]])
      }
    }
    "shooting" should {
      "spawn a bullet" in {
        val inbox = TestInbox[Command]()
        zombieActor run Shoot(inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[BulletSpawned])
      }
    }
  }

