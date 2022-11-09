package model

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import controller.{GameLoopActor, ViewMessage}
import model.GameData.{GameEntity, GameSeq}
import model.actors.ModelMessage
import model.entities.*
import model.entities.WorldSpace.{LanesLength, Position}
import model.entities.{BasicZombie, Bullet, Entity, PeaBullet, Plant, Troops, Zombie, Bullets}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.language.implicitConversions

class GameDataTest extends AnyFlatSpec with Matchers :

  val seedActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("seed")
  val zombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("zombie")
  val plantActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("plant")

  val bullet: GameEntity[Entity] = GameEntity(seedActor.ref, Bullets.ofType[PeaBullet] withPosition(1, LanesLength))
  val zombie: GameEntity[Entity] = GameEntity(zombieActor.ref, Troops.ofType[BasicZombie] withPosition(1, LanesLength))
  val shooter: GameEntity[Entity] = GameEntity(plantActor.ref, Troops.shooterOf[PeaBullet] withPosition(1, LanesLength / 2))

  val seq: GameSeq = GameSeq(List(bullet, zombie, shooter))

  import GameData.*

  "a seq" should "make seq of bullet" in {
    assertResult(seq.of[Bullet])(List(bullet))
  }

  "a seq" should "make seq of troop" in {
    assertResult(seq.of[Troop])(List(zombie, shooter))
  }

  "a seq" should "delete a dead element" in {
    assertResult(seq :- bullet.ref)(List(zombie, shooter))
  }


