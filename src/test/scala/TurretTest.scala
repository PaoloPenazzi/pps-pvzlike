import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import Turret.*

class TurretTest extends AnyFunSuite:
  test("An new turret should have 100 hp and be placed in the correct position") {
    val turret = Turret(50, 50)
    assert(turret.hp == 100)
    assert(turret.position == (50, 50))
  }