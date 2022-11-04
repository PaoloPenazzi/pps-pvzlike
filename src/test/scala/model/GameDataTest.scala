package model

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import controller.{GameLoopActor, ViewMessage}
import model.GameData.{GameEntity, GameSeq}
import model.actors.ModelMessage
import model.entities.WorldSpace.{LanesLength, Position}
import model.entities.{Bullet, Zombie, Entity, PeaBullet, PeaShooter, Plant, BasicZombie}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameDataTest extends AnyFlatSpec with BeforeAndAfter with Matchers :

  val seedActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("seed")
  val zombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("zombie")
  val plantActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("plant")

  val bullet: GameEntity[ModelMessage, Entity] = GameEntity(seedActor.ref, PeaBullet(1, LanesLength))
  val zombie: GameEntity[ModelMessage, Entity] = GameEntity(zombieActor.ref, BasicZombie((1, LanesLength)))
  val shooter: GameEntity[ModelMessage, Entity] = GameEntity(plantActor.ref, PeaShooter(Position(1, LanesLength / 2)))

  val seq: GameSeq[ModelMessage] = GameSeq(List(bullet, zombie, shooter))

  import GameData.*

  "a seq" should "make seq of turret" in {
    assertResult(seq.ofType[Plant])(List(shooter))
  }

  "a seq" should "make seq of zombie" in {
    assertResult(seq.ofType[Zombie])(List(zombie))
  }

  "a seq" should "make seq of bullet" in {
    assertResult(seq.ofType[Bullet])(List(bullet))
  }


