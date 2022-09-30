package model.actors

import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.entities.{Bullet, Seed}
import model.actors.{BulletMessages}

class BulletActorTest extends AnyWordSpec with BeforeAndAfterAll with Matchers:

  val bullet: Bullet = new Seed()
  val bulletActor: BehaviorTestKit[BulletMessages] = BehaviorTestKit(BulletActor(bullet))