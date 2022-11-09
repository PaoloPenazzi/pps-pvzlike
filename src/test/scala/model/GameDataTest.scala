package model

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import controller.ViewMessage
import controller.actors.GameLoopActor
import model.GameData.GameSeq.{GameSeq, GameSeqImpl}
import model.GameData.{GameEntity, GameSeq}
import model.actors.ModelMessage
import model.entities.WorldSpace.{LanesLength, Position}
import model.entities.*
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

  val seq: GameSeq = GameSeqImpl(List(bullet, zombie, shooter))

  import GameData.*

  "A seq" should "makes seq of bullet" in {
    assertResult(seq.of[Bullet])(List(bullet))
  }

  "A seq" should "makes seq of troop" in {
    assertResult(seq.of[Troop])(List(zombie, shooter))
  }

  "A seq" should "deletes a dead element" in {
    assertResult(seq :- bullet.ref)(List(zombie, shooter))
  }

  "A seq" should "updates its elements" in {
    val newBullet: GameEntity[Entity] = GameEntity(seedActor.ref, Bullets.ofType[PeaBullet] withPosition(2, LanesLength))
    assertResult(seq updateWith newBullet)(List(newBullet, zombie, shooter))
  }


