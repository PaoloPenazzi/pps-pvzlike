package model

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import controller.{GameLoopActor, ViewMessage}
import model.GameData.{GameEntity, GameSeq}
import model.actors.ModelMessage
import model.entities.WorldSpace.LanesLength
import model.entities.{Bullet, Enemy, Entity, PeaBullet, PeaShooter, Turret, Zombie}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameDataTest extends AnyFlatSpec with BeforeAndAfter with Matchers :
  var seedActor: TestInbox[ModelMessage] = _
  var zombieActor: TestInbox[ModelMessage] = _
  var plantActor: TestInbox[ModelMessage] = _

  var bullet: GameEntity[ModelMessage, Entity] = _
  var zombie: GameEntity[ModelMessage, Entity] = _
  var shooter: GameEntity[ModelMessage, Entity] = _

  var seq: GameSeq[ModelMessage] = _

  before {
    seedActor = TestInbox[ModelMessage]("seed")
    zombieActor = TestInbox[ModelMessage]("zombie")
    plantActor = TestInbox[ModelMessage]("plant")

    bullet = GameEntity(seedActor.ref, PeaBullet(1, LanesLength))
    zombie = GameEntity(zombieActor.ref, Zombie((1, LanesLength)))
    shooter = GameEntity(plantActor.ref, PeaShooter(1, LanesLength / 2)())

    seq = GameSeq(List(bullet, zombie, shooter))
  }

  import GameData.GameSeq.*

  "a seq" should "make seq of turret" in {
    assertResult(seq.ofType[Turret])(List(shooter))
  }

