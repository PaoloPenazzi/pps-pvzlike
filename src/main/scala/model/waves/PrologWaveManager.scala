package model.waves

import alice.tuprolog.*
import scala.io.Source

object PrologWaveManager:
  
  object PrologEngine:

    trait Engine {
      def solve: Term => LazyList[SolveInfo]
    }

    case class PrologEngine(theory: Theory) extends Engine {
      val engine: Prolog = new Prolog
      engine.setTheory(theory)

      override def solve: Term => LazyList[SolveInfo] = term =>
        LazyList.continually(engine solve term)

    }

  object PrologTheory:
    val pathTheory = "prolog/waves.pl"

    def getStringTheory(resourcePath: String): String = Source.fromResource(resourcePath).mkString

    def getTheory(stringTheory: String): Theory = Theory.parseWithStandardOperators(stringTheory)
    
  object WaveTerm:
    
    def intToTerm(value: Int): Term = Number.createNumber(value.toString)
