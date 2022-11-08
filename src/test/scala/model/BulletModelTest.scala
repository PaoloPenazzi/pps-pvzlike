package model

import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import model.entities
import model.entities.WorldSpace.Position
import model.entities.{Bullets, PawBullet, PeaBullet, Troops}

import scala.language.{implicitConversions, postfixOps}
import scala.concurrent.duration.FiniteDuration

class BulletModelTest extends AnyFlatSpec with should.Matchers:

  val bullet = Bullets.ofType[PawBullet]
  val plant = Troops.shooterOf[PeaBullet]

  "A Bullet" should "update his position" in {
    val elapsedTime = FiniteDuration(32, "milliseconds")
    val expectedResult: Position = (bullet.position.y, bullet.position.x + (elapsedTime.length * bullet.velocity))
    val updatedBullet = bullet.update(elapsedTime, List.empty)
    assertResult(expectedResult)(updatedBullet.position)
  }
  "A Bullet" should "disappear after hitting" in {
    assertResult(true)(bullet shouldDisappearAfterHitting plant)
  }


