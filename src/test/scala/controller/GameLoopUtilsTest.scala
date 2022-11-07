package controller

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.event.ActorWithLogClass
import controller.GameLoopActor.GameLoopCommands.Command
import model.GameData.{GameEntity, GameSeq}
import model.Statistics.GameStatistics
import model.actors.{Collision, ModelMessage}
import model.common.Utilities.MetaData
import model.entities.*
import model.entities.WorldSpace.LanesLength
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameLoopUtilsTest extends AnyWordSpec with Matchers :

  val bulletActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("bullet")
  val secondBulletActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("secondBullet")
  val zombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("zombie")
  val secondZombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("secondZombie")
  val thirdZombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("thirdZombie")
  val plantActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("plant")
  val bullet: GameEntity[Entity] = GameEntity(bulletActor.ref, PeaBullet((1, LanesLength)))
  val secondBullet: GameEntity[Entity] = GameEntity(secondBulletActor.ref, CherryBullet((1, LanesLength)))
  val zombie: GameEntity[Entity] = GameEntity(zombieActor.ref, BasicZombie((1, LanesLength)))
  val secondZombie: GameEntity[Entity] = GameEntity(secondZombieActor.ref, BasicZombie((1, LanesLength)))
  val thirdZombie: GameEntity[Entity] = GameEntity(thirdZombieActor.ref, BasicZombie((2, LanesLength)))
  val shooter: GameEntity[Entity] = GameEntity(plantActor.ref, PeaShooter((1, LanesLength / 2)))

  import GameLoopActor.*
  import GameLoopActor.GameLoopUtils.*
  import GameLoopActor.GameLoopUtils.CollisionUtils.*

  "The game loop" when {
    "started" should {
      "detect collision" in {
        val entities = List(bullet, zombie, secondZombie)
        detectCollision(entities) shouldBe List((bullet, List(zombie, secondZombie)))
      }

      "detect multiple collision" in {
        val entities = List(secondBullet, zombie, secondZombie, thirdZombie)
        detectCollision(entities) shouldBe List((secondBullet, List(zombie, secondZombie, thirdZombie)))
      }

      "send collision message" in {
        Behaviors.setup {
          (ctx: ActorContext[Command]) =>
            sendCollisionMessage(bullet, zombie, ctx)
            bulletActor.expectMessage(Collision(zombie.entity, ctx.self))
            Behaviors.empty
        }
      }

      "detect the interests" in {
        val entities = List(shooter, zombie, secondZombie)
        detectInterest(entities) shouldBe List((shooter.ref, List(zombie.entity, secondZombie.entity)),
          (zombie.ref, List()), (secondZombie.ref, List()))
      }

      "check if the wave is over" in {
        val entities = List(shooter, zombie)
        isWaveOver(entities) shouldBe false
      }

      "create a new wave of zombies" in {
        Behaviors.setup {
          (ctx: ActorContext[Command]) =>
            createWave(ctx).getClass shouldBe Seq.getClass
            Behaviors.empty
        }
      }

      "update rounds" in {
        updateRoundStats(GameStatistics()) shouldBe GameStatistics(List.empty, 1)
      }

      "update troop played" in {
        updateEntityStats(GameStatistics(), shooter.entity) shouldBe GameStatistics(List(shooter.entity))
      }

      "can't place two plant in the same place" in {
        isCellFree(PeaShooter((1, LanesLength / 2)), List(shooter, zombie)) shouldBe false
      }

      "can't place plant without enough money" in {
        enoughSunFor(PeaShooter(), MetaData()) shouldBe false
      }
    }
  }


