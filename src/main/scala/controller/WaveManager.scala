package controller

import controller.Generator.WaveImpl
import model.entities.WorldSpace.given
import model.entities.{Enemy, Zombie}

trait Wave:
  def waveNumber: Int
  def enemies: List[Enemy]
  
trait WaveGenerator:
  def generateNextWave: Wave

object Generator:
  def apply(): WaveGenerator = WaveGeneratorImpl()

  private case class WaveImpl(override val waveNumber: Int, override val enemies: List[Enemy]) extends Wave

  private case class WaveGeneratorImpl() extends WaveGenerator:
    private var waveNumber: Int = 0
    
    override def generateNextWave: Wave =
      waveNumber = waveNumber + 1
      val newEnemies = List.fill(2)(Zombie((1, 100)))
      WaveImpl(waveNumber, newEnemies)
      

