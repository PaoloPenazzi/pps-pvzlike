package model

import model.entities.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class BulletModelTest extends AnyFlatSpec with should.Matchers:
  "A bullet" should "update his position" in {
    val bullet: Seed = Seed()
    bullet.position = (10, 1)
    val timeElapsed = 5
    val expectedResult: (Double, Int) = (bullet.position._1 + (timeElapsed * bullet.velocity), 1)
    bullet updatePositionAfter timeElapsed
    assertResult(expectedResult)(bullet.position)
  }
