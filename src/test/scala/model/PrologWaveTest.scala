package model

import model.waves.PrologWaveManager.*
import model.waves.PrologWaveManager.PrologEngine.PrologEngine
import alice.tuprolog.SolveInfo
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class PrologWaveTest extends AnyFlatSpec with should.Matchers:
  val pathTheory = "prolog/waves.pl"
  val stringTheory: String = PrologTheory.getStringTheory(pathTheory)
  val engine: PrologEngine = PrologEngine(PrologTheory.getTheory(stringTheory))
  val query: String = "wave(6, L)"
  val solution: LazyList[SolveInfo] = engine solve WaveTerm.queryToTerm(query)

