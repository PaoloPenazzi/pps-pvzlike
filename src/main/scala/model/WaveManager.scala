package model

import scala.util.Random
import model.Generator.WaveImpl
import model.entities.WorldSpace.*
import model.entities.{Enemy, Zombie, FastZombie, WarriorZombie}

import scala.annotation.tailrec

/**
 * A wave of [[Enemy]].
 */
trait Wave:
  /**
   * @return The number of the current [[Wave]]
   */
  def waveNumber: Int

  /**
   * @return The list of [[Enemy]] spawned.
   */
  def enemies: List[Enemy]

/**
 * A generator of waves. It's not possible to specify the wave number.
 */
trait WaveGenerator:
  /**
   * @return The next [[Wave]]
   */
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
        case _ => createEnemyList(n - 1)(l = l :+ WarriorZombie())