package model

import model.entities.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import WorldSpace.{Position, given}

import scala.language.implicitConversions
import scala.concurrent.duration.FiniteDuration

class BulletModelTest extends AnyFlatSpec with should.Matchers:
  "A bullet" should "update his position" in {
    val bullet: PeaBullet = PeaBullet((10, 1))
    val elapsedTime = FiniteDuration(32, "milliseconds")
    val expectedResult: Position = (bullet.position.y, bullet.position.x + (elapsedTime.length * bullet.velocity))
    val updatedBullet = bullet.update(elapsedTime, List.empty)
    assertResult(expectedResult)(updatedBullet.position)
  }
