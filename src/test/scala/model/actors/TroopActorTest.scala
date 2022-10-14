package model.actors

import akka.actor.ActorSystem
import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, ScalaTestWithActorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import controller.Command
import controller.GameLoopActor.GameLoopCommands.{EntityDead, EntitySpawned, EntityUpdated, GameLoopCommand}
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

  val dummyPlant: Turret = Plant(1, LanesLength)()
  val dummyZombie: Zombie = Zombie((1, LanesLength + 2))
  val lowHealthPlant: Turret = Plant(1, LanesLength)(25)
  val turretActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(dummyPlant))
  val zombieActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(dummyZombie))
  val lowHealthTurretActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(TroopActor(lowHealthPlant))

  "The troop actor" when {
    "created" should {
      "be alive" in {
        turretActor.isAlive must be(true)
      }
    }
  }

  "The troop actor (turret)" when {
    "updated" should {
      "attack the zombie" in {
        val inbox = TestInbox[Command]()
        turretActor run Update(FiniteDuration(32, MILLISECONDS), List(dummyZombie), inbox.ref)
        turretActor expectEffect Effect.TimerScheduled("Shooting",
          Shoot(inbox.ref),
          dummyPlant.fireRate.seconds,
          Effect.TimerScheduled.SingleMode, false)(null)
      }
      "update his position" in {
        val inbox = TestInbox[Command]()
        turretActor run Update(FiniteDuration(32, MILLISECONDS), List(dummyZombie), inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityUpdated[Entity]])
      }
    }
    "shooting" should {
      "spawn a bullet" in {
        val inbox = TestInbox[Command]()
        turretActor run Shoot(inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntitySpawned[Bullet]])
      }
    }
    "colliding with a bullet" should {
      "update himself" in {
        val inbox = TestInbox[Command]()
        turretActor run Collision(PeaBullet(1,1), inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityUpdated[Entity]])
      }
    }
    "colliding with a bullet" should {
      "die if has 0 HPs" in {
        val inbox = TestInbox[Command]()
        lowHealthTurretActor run Collision(PeaBullet(1, 1), inbox.ref)
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
        zombieActor run Update(FiniteDuration(32, MILLISECONDS), List(dummyPlant), inbox.ref)
        zombieActor expectEffect Effect.TimerScheduled("Shooting",
          Shoot(inbox.ref),
          dummyZombie.fireRate.seconds,
          Effect.TimerScheduled.SingleMode, false)(null)
      }
      "update his position" in {
        val inbox = TestInbox[Command]()
        zombieActor run Update(FiniteDuration(32, MILLISECONDS), List(dummyPlant), inbox.ref)
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
        assert(message.isInstanceOf[EntitySpawned[Bullet]])
      }
    }
  }

