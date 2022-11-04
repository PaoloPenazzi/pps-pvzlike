import model.waves.{Generator, Wave, WaveGenerator}
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

class WaveTest extends AnyFlatSpec with should.Matchers:
  "Wave generator" should "increment the wave number consistently" in {
    val generator: WaveGenerator = Generator()
    generator.generateNextWave
    generator.generateNextWave
    val testWave: Wave = generator.generateNextWave
    assertResult(3)(testWave.waveNumber)
  }

  "The fourth wave" should "have 7 zombies" in {
    val generator: WaveGenerator = Generator()
    generator.generateNextWave
    generator.generateNextWave
    generator.generateNextWave
    val testWave: Wave = generator.generateNextWave
    assertResult(7)(testWave.enemies.size)
  }
