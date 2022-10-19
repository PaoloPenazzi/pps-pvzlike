package model

import model.entities.{MetaData, PeaShooter, Sun, Turret}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ResourceModelTest extends AnyFlatSpec with Matchers:

  val mockSun: Sun = Sun()
  val anotherMockSun: Sun = Sun()
  val turret: Turret = PeaShooter(1,0)()
  val metaData: MetaData = MetaData()

  "A sun" should "sum with other sun" in {
    mockSun + anotherMockSun shouldBe Sun(50)
  }

  "A spawned sun" should "sum with the actual sun" in {
    metaData addSun mockSun shouldBe MetaData(Sun())
  }

  "When a new turret is available" should "added at the list" in {
    metaData addAvailableTurret turret shouldBe MetaData(availableEntities = List(turret))
  }

