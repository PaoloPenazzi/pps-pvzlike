package model.common

import model.entities.*
import model.entities.WorldSpace.{LanesLength, NumOfLanes, Position, CellLength}
import scala.language.implicitConversions
import scala.util.Random

object DefaultValues:
  val endGameLimit: Int = -5
  val defaultPlantState: TroopState = TroopState.Idle
  val peashooterDefaultLife: Int = 100
  val wallnutDefaultLife: Int = 150
  val basicZombieDefaultLife: Int = 100
  val fastZombieDefaultLife: Int = 80
  val warriorZombieDefaultLife: Int = 200
  def generateZombieSpawnPosition: Position = (Random.between(0, NumOfLanes), LanesLength + Random.between(0, 20))
  
  val bullets: Troop => Bullet =
    case p: PeaShooter => PeaBullet(p.pointOfShoot)
    case c: CherryBomb => CherryBullet(c.position)

  val width: Entity => Float =
    case _: CherryBullet => CellLength*2
    case _: Troop => CellLength/2
    case _ => CellLength/5

  val fireRates: AttackingAbility => Int =
    case _: CherryBomb => 1
    case _: PeaShooter => 2
    case _: BasicZombie => 3
    case _: FastZombie => 2
    case _: WarriorZombie => 4
    case _ => 0

  val costs: Plant => Int =
    case _: CherryBomb => 150
    case _: PeaShooter => 100
    case _: Wallnut => 50
    case _  => 0

  val damages: Bullet => Int =
    case _: CherryBullet => 1000
    case _: PeaBullet => 25
    case _: SwordBullet => 60
    case _: PawBullet => 25
    case _ => 0

  val velocity: Entity => Float =
    case _: PeaBullet => 0.06
    case _: SwordBullet => -0.1
    case _: PawBullet => -0.1
    case _ => 0

  val ranges: AttackingAbility => Int =
    case _: CherryBomb => 1000
    case _: PeaShooter => 80
    case _: BasicZombie => 10
    case _: FastZombie => 15
    case _: WarriorZombie => 10
    case _ => 0

