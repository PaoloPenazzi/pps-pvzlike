package model

import alice.tuprolog.SolveInfo
import model.entities.{BasicZombie, FastZombie, WarriorZombie}
import model.waves.PrologWaveManager.*
import model.waves.PrologWaveManager.PrologEngine.PrologEngine
import model.waves.PrologWaveManager.PrologTheory.given
import model.waves.PrologWaveManager.WaveTerm.given
import model.waves.PrologWaveManager.PrologSolution.given
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.implicitConversions

class PrologWaveTest extends AnyFlatSpec with should.Matchers :
  val pathTheory = "prolog/waves.pl"
  val engine: PrologEngine = PrologEngine(pathTheory)

  "The engine" should "create a 1-length solution" in {
    val query: String = "wave(1, L)"
    val solution: LazyList[SolveInfo] = engine solve query
    solution.head.size shouldEqual 1
  }

  "The engine" should "create a correct solution" in {
    val query: String = "wave(6, L)"
    val solution: LazyList[SolveInfo] = engine solve query
    solution.head.foldRight(0)((e, acc) => {
      e match
        case _: BasicZombie => acc + 1
        case _: FastZombie => acc + 2
        case _: WarriorZombie => acc + 3
    }) shouldEqual 6
  }

