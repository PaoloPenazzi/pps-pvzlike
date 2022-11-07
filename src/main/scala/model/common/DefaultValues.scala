package model.common

import model.entities.*
import model.entities.WorldSpace.{LanesLength, NumOfLanes, Position, CellLength}
import scala.language.implicitConversions
import scala.util.Random

object DefaultValues:
  val endGameLimit: Int = -5
  val basicZombieDefaultLife: Int = 100
  val fastZombieDefaultLife: Int = 80
  val warriorZombieDefaultLife: Int = 200

  def generateZombieSpawnPosition: Position = (Random.between(0, NumOfLanes), LanesLength + Random.between(0, 20))
