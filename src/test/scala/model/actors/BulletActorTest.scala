package model.actors

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import controller.actors.GameLoopActor.GameLoopCommands.{Command, EntityDead, EntityUpdated}
import model.entities.*
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers.must
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}
import scala.language.implicitConversions

class BulletActorTest extends AnyWordSpec with BeforeAndAfterAll with Matchers :

  val bullet: Bullet = Bullets.ofType[PeaBullet]
  val bulletActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(BulletActor(bullet))
  val peaShooter = Troops.shooterOf[PeaBullet]

  "The troop actor" when {
    "created" should {
      "be alive" in {
        bulletActor.isAlive must be(true)
      }
    }
  }

  "The bullet actor (turret)" when {
    "updated" should {
      "update his position" in {
        val inbox = TestInbox[Command]()
        bulletActor run Update(FiniteDuration(32, MILLISECONDS), List(), inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityUpdated[Entity]])
      }
    }
    "collision" should {
      "Disappear" in {
        val inbox = TestInbox[Command]()
        bulletActor run Collision(peaShooter, inbox.ref)
        assert(inbox.hasMessages)
        val message = inbox.receiveMessage()
        assert(message.isInstanceOf[EntityDead[Entity]])
      }
    }
  }

