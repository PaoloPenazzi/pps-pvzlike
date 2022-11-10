package model.waves


import alice.tuprolog.{Prolog, SolveInfo, Term, Theory}
import model.entities.*

import java.util.Scanner
import scala.io.Source
import scala.language.implicitConversions

/** Creates a fanciful [[Seq]] of [[Zombie]] based on a given [[power]],
 * using TuProlog [[alice.tuprolog.Engine]]. */
object PrologWaveManager:

  /** Contains constructs for building and launching a Prolog engine. */
  object PrologEngine:

    /** Defines an Engine, designed for our purposes. */
    trait Engine:
      /**
       * Solve a [[Term]]
       *
       * @return A [[LazyList]] of solutions.
       */
      def solve: Term => LazyList[SolveInfo]

      /**
       * Generate a wave.
       *
       * @param power the [[Wave]] power.
       * @return A [[List]] of different types of [[Zombie]].
       */
      def generateWave(power: Int): List[Zombie]

    /** Implementations of [[Engine]].
     *
     * @param theory the theory that the [[Engine]] uses to solve the input query.
     */
    case class PrologEngine(theory: Theory) extends Engine :
      val engine: Prolog = new Prolog
      engine.setTheory(theory)

      override def generateWave(power: Int): List[Zombie] =
        import WaveTerm.given
        val query: String = "wave(" + power + ", L)"
        val solution: LazyList[SolveInfo] = solve(query)
        PrologSolution.waveFromPrologSolution(solution.head)

      override def solve: Term => LazyList[SolveInfo] = term =>
        LazyList.continually(engine solve term)

  /**
   * Contains useful operator for building [[Theory]].
   */
  object PrologTheory:
    given Conversion[String, Theory] = e => Theory.parseWithStandardOperators(getStringTheory(e))

    private def getStringTheory(resourcePath: String): String = Source.fromResource(resourcePath).mkString

  /** Contains useful operator for building [[Term]]. */
  object WaveTerm:
    given Conversion[String, Term] = Term.createTerm(_)

  /** Contains useful method for deserializing TuProlog solutions. */
  object PrologSolution:
    /**
     * Transform the TuProlog [[model.waves.PrologWaveManager.PrologEngine]]'s output in a [[List]] of [[Zombie]].
     *
     * @param solution the [[PrologEngine]] output.
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

