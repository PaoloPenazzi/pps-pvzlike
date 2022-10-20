package model

import scala.util.Random
import model.Generator.WaveImpl
import model.entities.WorldSpace.*
import model.entities.{Enemy, Zombie}

import scala.annotation.tailrec

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
      val newEnemies = createEnemyList(2 * waveNumber - 1)(List.empty[Enemy])
      WaveImpl(waveNumber, newEnemies)

    @tailrec
    private def createEnemyList(n: Int)(l: List[Enemy]): List[Enemy] =
      n match
        case 0 => l
        case _ => createEnemyList(n - 1)(l = l :+ new Zombie((Random.between(1, NumOfLanes+1), LanesLength - 5)))