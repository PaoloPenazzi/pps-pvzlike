package controller.utils

import akka.actor.typed.scaladsl.ActorContext
import controller.actors.GameLoopActor.GameLoopCommands.Command
import model.GameData.GameEntity
import model.GameData.GameSeq.given
import model.actors.Collision
import model.entities.{Bullet, Entity, Troop}

import scala.language.{implicitConversions, postfixOps}

/** A specific object for collision management */
object CollisionUtils:
  /** Manages the whole collision's process:
   * checks the [[Collision]] and, in case, sends [[Collision]] messages.
   *
   * @param entities the entities in game.
   * @param ctx      the [[ActorContext]] that wants to receive the replies.
   */
  def handleCollision(entities: Seq[GameEntity[Entity]], ctx: ActorContext[Command]): Unit =
    checkCollision(entities) filter (_.collidedEntities.nonEmpty) foreach { e =>
      if e.entity.entity hitMultipleTimes
      then e.collidedEntities foreach { r => e.sendCollisionMessages(r, ctx) }
      else {
        e.sendCollisionMessages(e.collidedEntities.head, ctx)
      }
    }

  /** Detects [[Collision]] between all entities in game.
   *
   * @param entities the entities in game
   * @return
   */
  def checkCollision(entities: Seq[GameEntity[Entity]]): Seq[BulletTroopCollision] =
    for
      b <- entities.of[Bullet]
    yield
      BulletTroopCollision(b, for
        e <- entities.of[Troop]
        if b.entity isCollidingWith e.entity
      yield e)

  /** Defines a collision between two [[GameEntity]] */
  trait Collision:
    /** The type of the first [[GameEntity]] that has collided. */
    type A <: Entity

    /** The type of [[GameEntity]] that makes up
     * the sequence of [[Entity]] that collided with another [[GameEntity]].
     */
    type B <: Entity

    /**
     *
     * @return the [[GameEntity]] with [[A]] type.
     */
    def entity: GameEntity[A]

    /**
     *
     * @return the [[GameEntity]]'s sequence with [[B]] type collided with [[entity]].
     */
    def collidedEntities: Seq[GameEntity[B]]

    /** Send [[Collision]] messages to both [[entity]] and a [[GameEntity]] that has collided with him.
     *
     * @param receiver the [[GameEntity]] collided with [[entity]].
     * @param ctx      the [[ActorContext]] that wants to receive the replies.
     */
    def sendCollisionMessages(receiver: GameEntity[B], ctx: ActorContext[Command]): Unit =
      entity.ref ! Collision(receiver.entity, ctx.self)
      receiver.ref ! Collision(entity.entity, ctx.self)

  /** An implementation of a [[Collision]] between a [[Bullet]] and a [[Troop]].
   *
   * @param bullet         the [[GameEntity]] involved in the collision.
   * @param troopsCollided the [[Seq]] of [[GameEntity]] involved in the collision.
   */
  case class BulletTroopCollision(bullet: GameEntity[Bullet], troopsCollided: Seq[GameEntity[Troop]]) extends Collision :
    override type A = Bullet
    override type B = Troop

    override def entity: GameEntity[Bullet] = bullet

    override def collidedEntities: Seq[GameEntity[Troop]] = troopsCollided
