package model.waves

import model.entities.WorldSpace.*
import model.entities.{BasicZombie, FastZombie, WarriorZombie, Zombie}
import model.waves.Generator.WaveImpl
import model.waves.{Wave, WaveGenerator}


import scala.annotation.tailrec
import scala.util.Random

/**
 * A wave of [[Zombie]].
 */
trait Wave:
  /**
   * @return The number of the current [[Wave]]
   */
  def waveNumber: Int

  /**
   * @return The list of [[Zombie]] spawned.
   */
  def enemies: List[Zombie]

/**
 * A generator of waves. It's not possible to specify the wave number.
 */
trait WaveGenerator:
  def resetWaves(): Unit
  /**
   * @return The next [[Wave]]
   */
  def generateNextWave: Wave

object Generator:
  def apply(): WaveGenerator = WaveGeneratorImpl()

  private case class WaveImpl(override val waveNumber: Int, override val enemies: List[Zombie]) extends Wave

  private case class WaveGeneratorImpl() extends WaveGenerator:
    private var waveNumber: Int = 0

    override def resetWaves(): Unit = waveNumber = 0

    override def generateNextWave: Wave =
      waveNumber = waveNumber + 1
      val newEnemies = createEnemyList(2 * waveNumber - 1)(List.empty[Zombie])
      WaveImpl(waveNumber, newEnemies)

    @tailrec
    private def createEnemyList(n: Int)(l: List[Zombie]): List[Zombie] =
      n match
        case 0 => l
        case _ => createEnemyList(n - 1)(l = l :+ BasicZombie())