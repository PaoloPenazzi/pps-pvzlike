package model

import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*
import model.entities.{Bullets, CherryBullet, FastZombie, PawBullet, PeaBullet, Troops, WarriorZombie}
import model.entities.WorldSpace.Position

import scala.language.{implicitConversions, postfixOps}
import scala.concurrent.duration.FiniteDuration

class BulletModelTest extends AnyFlatSpec with should.Matchers:

  val bullet = Bullets.ofType[PawBullet]
  val plant = Troops.shooterOf[PeaBullet]
  val cherryBullet = Bullets.ofType[CherryBullet]
  val fastZombie = Troops.ofType[FastZombie].withPosition(Position(1, 0))
  val warriorZombie = Troops.ofType[WarriorZombie].withPosition(Position(0, 1))

  "A Bullet" should "update his position" in {
    val elapsedTime = FiniteDuration(32, "milliseconds")
    val expectedResult: Position = (bullet.position.y, bullet.position.x + (elapsedTime.length * bullet.velocity))
    val updatedBullet = bullet.update(elapsedTime, List.empty)
    assertResult(expectedResult)(updatedBullet.position)
  }
  "A Bullet" should "disappear after hitting" in {
    assertResult(true)(bullet shouldDisappearAfterHitting plant)
  }
  "A Bullet" should "collide with a Troop in his range" in {
    assertResult(true)(bullet checkCollisionWith plant)
  }
  "A PawBullet" should "apply a correct damage" in {
    assertResult(plant.life - bullet.damage)((bullet applyDamage plant).life)
  }
  "A CherryBullet" should "collide with more entities and in different lane" in {
    assertResult(true)(cherryBullet collideWith fastZombie)
    assertResult(true)(cherryBullet collideWith warriorZombie)
  }


