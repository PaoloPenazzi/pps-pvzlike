import model.entities.{BasicZombie, Zombie}
import model.waves.WaveGenerator.Wave
import model.waves.WaveGenerator
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.reflect.ClassTag

class WaveTest extends AnyFlatSpec with should.Matchers:
  "Wave generator" should "create a basic wave of zombies" in {
    WaveGenerator.generateNextBasicWave(5).enemies.forall(e => e.isInstanceOf[BasicZombie])
  }

  "Wave generator" should "create a fanciful wave of zombies with max 9 zombies" in {
    WaveGenerator.generateNextWave(5).enemies.size shouldBe <= (13)
  }

  "Wave generator" should "throw an exception if the round is less then 0" in {
    assertThrows(WaveGenerator.generateNextBasicWave(0))(ClassTag(classOf[IllegalArgumentException]))
  }

  "Wave prolog generator" should "throw an exception if the round is less then 0" in {
    assertThrows(WaveGenerator.generateNextWave(0))(ClassTag(classOf[IllegalArgumentException]))
  }
