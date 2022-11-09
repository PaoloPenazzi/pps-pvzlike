package controller

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.event.ActorWithLogClass
import controller.actors.GameLoopActor.*
import controller.actors.GameLoopActor.GameLoopCommands.Command
import controller.utils.CollisionUtils.*
import controller.utils.GameLoopUtils.*
import model.GameData.{GameEntity, GameSeq}
import model.Statistics.GameStatistics
import model.actors.{Collision, ModelMessage}
import model.common.Utilities.MetaData
import model.entities.*
import model.entities.WorldSpace.LanesLength
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.language.implicitConversions

class GameLoopUtilsTest extends AnyWordSpec with Matchers :

  val bulletActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("bullet")
  val secondBulletActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("secondBullet")
  val zombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("zombie")
  val secondZombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("secondZombie")
  val thirdZombieActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("thirdZombie")
  val plantActor: TestInbox[ModelMessage] = TestInbox[ModelMessage]("plant")
  val bullet: GameEntity[Entity] = GameEntity(bulletActor.ref, Bullets.ofType[PeaBullet] withPosition(1, LanesLength))
  val secondBullet: GameEntity[Entity] = GameEntity(secondBulletActor.ref, Bullets.ofType[CherryBullet] withPosition(1, LanesLength))
  val zombie: GameEntity[Entity] = GameEntity(zombieActor.ref, Troops.ofType[BasicZombie] withPosition(1, LanesLength))
  val secondZombie: GameEntity[Entity] = GameEntity(secondZombieActor.ref, Troops.ofType[BasicZombie] withPosition(1, LanesLength))
  val thirdZombie: GameEntity[Entity] = GameEntity(thirdZombieActor.ref, Troops.ofType[BasicZombie] withPosition(2, LanesLength))
  val shooter: GameEntity[Entity] = GameEntity(plantActor.ref, Troops.shooterOf[PeaBullet] withPosition(1, LanesLength / 2))

  "The game loop" when {
    "started" should {
      "detect collision" in {
        val entities = List(bullet, zombie, secondZombie)
        checkCollision(entities) shouldBe List(BulletTroopCollision(bullet.asInstanceOf[GameEntity[Bullet]],
          List(zombie.asInstanceOf[GameEntity[Troop]], secondZombie.asInstanceOf[GameEntity[Troop]])))
      }

      "detect multiple collision" in {
        val entities = List(secondBullet, zombie, secondZombie, thirdZombie)
        checkCollision(entities) shouldBe List(BulletTroopCollision(secondBullet.asInstanceOf[GameEntity[Bullet]],
          List(zombie.asInstanceOf[GameEntity[Troop]],
            secondZombie.asInstanceOf[GameEntity[Troop]], thirdZombie.asInstanceOf[GameEntity[Troop]])))
      }

      "send collision message" in {
        Behaviors.setup {
          (ctx: ActorContext[Command]) =>
            val collision: BulletTroopCollision = BulletTroopCollision(bullet.asInstanceOf[GameEntity[Bullet]], List(zombie.asInstanceOf[GameEntity[Troop]]))
            collision.sendCollisionMessages(zombie.asInstanceOf[GameEntity[Troop]], ctx)
            zombieActor.expectMessage(Collision(bullet.entity, ctx.self))
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
            assert(createWave(ctx, 2).nonEmpty)
            Behaviors.empty
        }
      }

      "update rounds" in {
        updateRoundStats(GameStatistics()) shouldBe GameStatistics(List.empty, 2)
      }

      "update troop played" in {
        updateEntityStats(GameStatistics(), shooter.entity) shouldBe GameStatistics(List(shooter.entity))
      }

      "can't place two plant in the same place" in {
        isCellFree(Troops.shooterOf[PeaBullet] withPosition(1, LanesLength / 2), List(shooter.asInstanceOf[GameEntity[Plant]])) shouldBe false
      }

      "can't place plant without enough money" in {
        enoughSunFor(Troops.shooterOf[PeaBullet], MetaData()) shouldBe false
      }
    }
  }


