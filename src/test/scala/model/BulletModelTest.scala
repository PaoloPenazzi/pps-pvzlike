package model

import model.entities.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import WorldSpace.{Position, given}

class BulletModelTest extends AnyFlatSpec with should.Matchers:
  "A bullet" should "update his position" in {
    val bullet: Seed = Seed()
    bullet.position = (10, 1)
    val timeElapsed = 5
    val expectedResult: Position = (bullet.position.y, bullet.position.x + (timeElapsed * bullet.velocity))
    bullet updatePositionAfter timeElapsed
    assertResult(expectedResult)(bullet.position)
  }
