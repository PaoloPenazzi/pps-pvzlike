package model.waves

import alice.tuprolog.SolveInfo
import model.entities.WorldSpace.*
import model.entities.{BasicZombie, FastZombie, WarriorZombie, Zombie}
import model.waves.PrologWaveManager.PrologEngine.PrologEngine
import model.waves.PrologWaveManager.{PrologSolution, PrologTheory, WaveTerm}
import model.waves.WaveGenerator.*

import scala.annotation.tailrec
import scala.util.Random

object WaveGenerator:
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

  case class WaveImpl(override val waveNumber: Int, override val enemies: List[Zombie]) extends Wave

  val pathTheory = "prolog/waves.pl"
  private val prolog: PrologEngine = PrologEngine(PrologTheory.getTheory(pathTheory))

  /**
   * Generates the next wave using Prolog.
   *
   * @param waveNumber the wave number.
   * @return The next [[Wave]].
   */
  def generateNextWave(waveNumber: Int): Wave =
    val enemies = prolog generateWave (waveNumber * 2 - 1)
    WaveImpl(waveNumber, enemies)

  /**
   * Generates the next wave using Scala. Only [[BasicZombie]] are spawned.
   *
   * @param waveNumber the wave number.
   * @return The next [[Wave]].
   */
  def generateNextBasicWave(waveNumber: Int): Wave =
    val newEnemies = createEnemyList(2 * waveNumber - 1)(List.empty[Zombie])
    WaveImpl(waveNumber, newEnemies)

  @tailrec
  private def createEnemyList(n: Int)(l: List[Zombie]): List[Zombie] =
    n match
      case 0 => l
      case _ => createEnemyList(n - 1)(l = l :+ BasicZombie())