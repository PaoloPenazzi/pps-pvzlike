package model.waves

import alice.tuprolog.{Engine, Number, Prolog, SolveInfo, Struct, Term, Theory}
import model.entities.{BasicZombie, FastZombie, Troops, WarriorZombie, Zombie}

import java.util.Scanner
import scala.io.Source
import scala.language.implicitConversions

object PrologWaveManager:
  
  object PrologEngine:

    trait Engine {
      /**
       * Solve a [[Term]]
       * @return A [[LazyList]] of solutions.
       */
      def solve: Term => LazyList[SolveInfo]

      /**
       * Generate a wave.
       * @param power the [[Wave]] power.
       * @return A [[List]] of different types of [[Zombie]].
       */
      def generateWave(power: Int): List[Zombie]
    }

    case class PrologEngine(theory: Theory) extends Engine {
      val engine: Prolog = new Prolog
      engine.setTheory(theory)

      override def solve: Term => LazyList[SolveInfo] = term =>
        LazyList.continually(engine solve term)

      override def generateWave(power: Int): List[Zombie] =
        import WaveTerm.given
        val query: String = "wave(" + power + ", L)"
        val solution: LazyList[SolveInfo] = solve(query)
        PrologSolution.waveFromPrologSolution(solution.head)
    }

  object PrologTheory:
    /**
     * @param resourcePath the path where the [[Theory]] file is located.
     * @return the [[Theory]]
     */
    def getTheory(resourcePath: String): Theory =
      val stringTheory = getStringTheory(resourcePath)
      Theory.parseWithStandardOperators(stringTheory)

    private def getStringTheory(resourcePath: String): String = Source.fromResource(resourcePath).mkString

  object WaveTerm:
    /** Converts a [[String]] in a [[Term]].
     * @return the corresponding [[Term]].
     */
    given Conversion[String, Term] = Term.createTerm(_)

  object PrologSolution:
    /**
     * Transform the Prolog output in a [[List]] of [[Zombie]].
     * @param solution the Prolog output.
     * @return a [[List]] of [[Zombie]].
     */
    def waveFromPrologSolution(solution: SolveInfo): List[Zombie] =
      waveFromTerm(solution.getTerm("L"))

    private def waveFromTerm(term: Term): List[Zombie] =
      term.toString.replaceAll("\\[|\\]", "")
                   .split(",")
                   .foldRight(List.empty[Int])((e, acc) => acc :+ e.toInt)
                   .map(e => e match
                     case 1 => BasicZombie()
                     case 2 => FastZombie()
                     case 3 => WarriorZombie())

