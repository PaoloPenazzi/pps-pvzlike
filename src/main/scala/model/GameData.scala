package model

import akka.actor.typed.ActorRef
import model.GameData.GameSelector.GameSelectorBuilder
import model.GameData.GameSeq.{GameSeq, GameSeqImpl}
import model.actors.ModelMessage
import model.entities.*

import scala.annotation.targetName

/** Represents the type of data handled by the [[controller.GameLoopActor.GameLoop]]. */
object GameData:

  /** Implements a view over the model entities, designed for the [[controller.GameLoopActor.GameLoop]].
   *
   * @param ref    the [[ActorRef]] of the [[entity]]'s actor.
   * @param entity the model subject.
   * @tparam E the type of [[entity]].
   */
  case class GameEntity[E <: Entity](ref: ActorRef[ModelMessage], entity: E)

  /** Extends [[Seq]] in order to do a particular tasks on the [[GameEntity]]. */
  object GameSeq:
    given Conversion[Seq[GameEntity[Entity]], GameSeq] = GameSeqImpl(_)

    /** Defines a [[GameSeq]] a [[Seq]] of [[GameEntity]] with special abilities. */
    trait GameSeq:
      /** Creates a [[Seq]], starting from [[seq]], composed only of [[GameEntity]] with type [[E]].
       *
       * @tparam E the type of [[GameSeq]] that you want.
       * @return a [[Seq]] made of [[GameEntity]] of [[E]]
       */
      def of[E <: Entity : GameSelectorBuilder]: Seq[GameEntity[E]] =
        seq.collect(summon[GameSelectorBuilder[E]].by)

      /** Deletes a [[GameEntity]] from the [[seq]].
       *
       * @param ref the [[ActorRef]] of the [[GameEntity]] that you want to delete.
       * @return the [[seq]] without [[ref]] element.
       */
      @targetName("delete")
      def :-(ref: ActorRef[ModelMessage]): Seq[GameEntity[Entity]] = seq filter (_.ref != ref)

      /** Updates the [[seq]] with the [[entity]].
       *
       * @param entity the that you want to update.
       * @return [[seq]] updated.
       */
      def updateWith(entity: GameEntity[Entity]): Seq[GameEntity[Entity]] =
        seq collect { case x if x.ref == entity.ref => entity case x => x }

      /**
       *
       * @return the [[Seq]] made of [[GameEntity]].
       */
      def seq: Seq[GameEntity[Entity]]

    /** Implements the [[GameSeq]].
     *
     * @param seq the [[Seq]] of [[GameEntity]] in game.
     */
    case class GameSeqImpl(seq: Seq[GameEntity[Entity]]) extends GameSeq

  /** Contains some ways of creating [[GameSeq]]. */
  object GameSelector:
    /** Filters the [[GameEntity]] whose type is [[E]].
     *
     * @tparam E the type to filter.
     */
    trait GameSelectorBuilder[E <: Entity]:
      def by: PartialFunction[GameEntity[Entity], GameEntity[E]]

    given GameSelectorBuilder[Troop] with
      override def by: PartialFunction[GameEntity[Entity], GameEntity[Troop]] = {
        case e if e.entity.isInstanceOf[Troop] => GameEntity(e.ref, e.entity.asInstanceOf[Troop])
      }

    given GameSelectorBuilder[Plant] with
      override def by: PartialFunction[GameEntity[Entity], GameEntity[Plant]] = {
        case e if e.entity.isInstanceOf[Plant] => GameEntity(e.ref, e.entity.asInstanceOf[Plant])
      }

    given GameSelectorBuilder[Bullet] with
      override def by: PartialFunction[GameEntity[Entity], GameEntity[Bullet]] = {
        case e if e.entity.isInstanceOf[Bullet] => GameEntity(e.ref, e.entity.asInstanceOf[Bullet])
      }






