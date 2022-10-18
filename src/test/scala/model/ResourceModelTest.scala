package model

import model.entities.Sun
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ResourceModelTest extends AnyFlatSpec with Matchers:

  val mockSun: Sun = Sun(30)()
  val anotherMockSun: Sun = Sun(40)()

  "A sun" should "sum with other sun" in {
    mockSun + anotherMockSun shouldBe Sun(70)()
  }

  "A sun" should "subtract with other sun" in {
    mockSun - anotherMockSun shouldBe Sun(-10)()
  }



