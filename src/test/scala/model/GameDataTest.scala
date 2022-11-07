package model

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import controller.{GameLoopActor, ViewMessage}
import model.GameData.{GameEntity, GameSeq}
import model.actors.ModelMessage
import model.entities.WorldSpace.{LanesLength, Position}
import model.entities.{BasicZombie, Bullet, Entity, PeaBullet, Plant, Troops, Zombie, Bullets}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.language.implicitConversions

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

    bullet = GameEntity(seedActor.ref, Bullets.ofType[PeaBullet] withPosition (1, LanesLength))
    zombie = GameEntity(zombieActor.ref, Troops.ofType[BasicZombie] withPosition (1, LanesLength))
    shooter = GameEntity(plantActor.ref, Troops.shooterOf[PeaBullet] withPosition (1, LanesLength / 2))

    seq = GameSeq(List(bullet, zombie, shooter))
  }

  import GameData.GameSeq.*

  "a seq" should "make seq of turret" in {
    assertResult(seq.ofType[Plant])(List(shooter))
  }

