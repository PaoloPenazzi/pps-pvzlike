package model.waves

import alice.tuprolog.SolveInfo
import model.entities.WorldSpace.*
import model.entities.{BasicZombie, FastZombie, WarriorZombie, Zombie}
import model.waves.PrologWaveManager.PrologEngine.PrologEngine
import model.waves.PrologWaveManager.PrologTheory.given
import model.waves.PrologWaveManager.{PrologSolution, PrologTheory, WaveTerm}
import model.waves.WaveGenerator.*

import scala.annotation.tailrec
import scala.language.implicitConversions
import scala.util.Random

/** Creates the [[Wave]]: a specific [[Seq]] of [[Zombie]] based on a given [[waveNumber]]. */
object WaveGenerator:
  val pathTheory = "prolog/waves.pl"
  private val prolog: PrologEngine = PrologEngine(pathTheory)

  /** Generates the next wave using Prolog.
   *
   * @param waveNumber the wave number. It shall be a positive integer.
   * @return The next [[Wave]].
   */
  def generateNextWave(waveNumber: Int): Wave =
    require(waveNumber > 0)
    val enemies = prolog generateWave powerOf(waveNumber)
    WaveImpl(waveNumber, enemies)

  /** Defines the formula of the wave's power.
   *
   * @return the power of the wave.
   */
  def powerOf: Int => Int = _ * 3 - 2

  /** Generates the next wave using Scala. Only [[BasicZombie]] are spawned.
   *
   * @param waveNumber the wave number. It shall be a positive integer.
   * @return The next [[Wave]].
   */
  def generateNextBasicWave(waveNumber: Int): Wave =
    require(waveNumber > 0)
    val newEnemies = createEnemyList(powerOf(waveNumber))(List.empty[Zombie])
    WaveImpl(waveNumber, newEnemies)

  @tailrec
  private def createEnemyList(n: Int)(l: Seq[Zombie]): Seq[Zombie] =
    n match
      case 0 => l
      case _ => createEnemyList(n - 1)(l = l :+ BasicZombie())

  /**
   * A wave of [[Zombie]].
   */
  trait Wave:
    /**
     * @return The number of the current [[Wave]]
     */
    def waveNumber: Int

    /**
     * @return The [[Seq]] of [[Zombie]] spawned.
     */
    def enemies: Seq[Zombie]

  /** Simple implementation of [[Wave]].
   *
   * @param waveNumber the wave number. It should be a positive integer.
   * @param enemies    the [[Zombie]]s that make up the [[Wave]].
   */
  case class WaveImpl(override val waveNumber: Int, override val enemies: Seq[Zombie]) extends Wave