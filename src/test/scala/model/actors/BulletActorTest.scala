package model.actors

import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import model.entities.{Bullet, Seed}

class BulletActorTest extends AnyWordSpec with BeforeAndAfterAll with Matchers:

  val bullet: Bullet = new Seed(1,0)
  val bulletActor: BehaviorTestKit[ModelMessage] = BehaviorTestKit(BulletActor(bullet))