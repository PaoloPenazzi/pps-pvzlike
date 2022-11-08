package model

import model.common.Utilities.{MetaData, Speed, Sun}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class MetaDataTest extends AnyFlatSpec with should.Matchers:

  "The metadata" should "change its speed" in {
    val meta = MetaData() >>> Speed.Fast
    meta.speed shouldBe Speed.Fast
    val updateMeta = meta >>> Speed.Slow
    updateMeta.speed shouldBe Speed.Slow
  }

  "The metadata" should "increase its sun value" in {
    val meta = MetaData() + Sun.Big.value
    meta.sun shouldBe Sun.Big.value
  }

  "The metadata" should "decrease its sun value" in {
    val meta = MetaData() + Sun.Normal.value
    val updateMeta = meta - Sun.Small.value
    updateMeta.sun shouldBe (Sun.Normal.value - Sun.Small.value)
  }



