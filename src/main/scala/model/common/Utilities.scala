package model.common

import model.entities.Plant

import scala.annotation.targetName
import scala.concurrent.duration.FiniteDuration

/** Contains some game-utility elements. */
object Utilities:

  /** Creates the [[MetaData]] of the game.
   * Contains some special information about the game in progress.
   *
   * @param sun   the actual value of user's sun.
   * @param speed the actual game's speed frequency.
   */
  case class MetaData(sun: Int = 0, speed: Speed = Speed.Normal):
    /** Adds a specific [[quantity]] of [[Sun]] at the [[MetaData]].
     *
     * @param quantity the sun's quantity to add.
     * @return the updated [[MetaData]].
     */
    @targetName("sum")
    def +(quantity: Int): MetaData = MetaData(quantity + sun, speed)

    /** Subtracts a specific [[quantity]] of [[Sun]] at the [[MetaData]].
     *
     * @param quantity the sun's quantity to subtract.
     * @return the updated [[MetaData]].
     */
    @targetName("subtraction")
    def -(quantity: Int): MetaData = MetaData(sun - quantity, speed)

    /** Changes the game's speed.
     *
     * @param newSpeed the new speed of the game.
     * @return the updated [[MetaData]].
     */
    @targetName("change speed")
    def >>>(newSpeed: Speed): MetaData = MetaData(sun, newSpeed)

  /** Defines the concepts of game and resources' speed. */
  enum Speed(val gameSpeed: FiniteDuration)(val resourceSpeed: FiniteDuration):
    case Normal extends Speed(FiniteDuration(16, "milliseconds"))(FiniteDuration(3, "seconds"))
    case Fast extends Speed(FiniteDuration(48, "milliseconds"))(FiniteDuration(1, "seconds"))

  /**
   * Defines the concepts of resources in game. Modelled as [[Sun]]
   * like in the real Plant Vs Zombies game.
   * The higher the [[value]] of the [[Sun]], the easier the game will be.
   */
  enum Sun(val value: Int):
    case Small extends Sun(15)
    case Normal extends Sun(50)
    case Big extends Sun(100)

