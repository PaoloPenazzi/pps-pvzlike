package model

import model.entities.Sun
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ResourceModelTest extends AnyFlatSpec with Matchers:

  val mockSun: Sun = Sun()
  val anotherMockSun: Sun = Sun()

  "A sun" should "sum with other sun" in {
    mockSun + anotherMockSun shouldBe Sun(50)
  }



