package model.actors

import akka.actor.ActorSystem
import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, ScalaTestWithActorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import controller.Command
import controller.GameLoop.GameLoopCommands.{EntityUpdate, GameLoopCommand}
import model.actors.{BulletActor, EntitySpawned, Hit, Shoot, TurretActor, TurretMessages, Update}
import model.common.DefaultValues.*
import model.entities.*
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

class TurretActorTest extends AnyWordSpec with BeforeAndAfterAll with Matchers:

  val plant: Turret = Plant(50, 1)
  val testZombie1: Enemy = Zombie()
  val turretActor: BehaviorTestKit[TurretMessages] = BehaviorTestKit(TurretActor(plant))
  val inbox = TestInbox[Command]()

  "The Turret Actor" when {
    "created" should {
      "be alive" in {
        turretActor.isAlive must be(true)
      }

      "shoot zombie" in {
        testZombie1.position = (100, 1)
        turretActor run Update(10, List(testZombie1), inbox.ref)
        turretActor expectEffect Effect.TimerScheduled("TurretShooting", Shoot(inbox.ref), plant.fireRate.seconds, Effect.TimerScheduled.SingleMode, false)(null)
      }

      "spawn a bullet" in {
        turretActor run Shoot(inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntitySpawned])
      }

      "should die" in {
        turretActor run Hit(150)
        turretActor run Hit(150)
        turretActor.isAlive must be(false)
      }
    }
  }
