import controller.{WaveGenerator, Wave, Generator}
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
