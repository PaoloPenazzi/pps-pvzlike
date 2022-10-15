package model.actors

import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.entities.{Bullet, PeaBullet}
import scala.language.implicitConversions

class BulletActorTest extends AnyWordSpec with BeforeAndAfterAll with Matchers:

  val bullet: Bullet = new PeaBullet(1,0)
  val bulletActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(BulletActor(bullet))