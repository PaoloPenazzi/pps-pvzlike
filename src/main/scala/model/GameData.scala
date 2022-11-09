package model

import akka.actor.typed.ActorRef
import model.GameData.GameSelector.GameSelectorBuilder
import model.GameData.GameSeq.{GameSeq, GameSeqImpl}
import model.actors.ModelMessage
import model.entities.*

import scala.annotation.targetName

/** Represents the type of data handled by the [[controller.GameLoopActor.GameLoop]]. */
object GameData:
  given Conversion[Seq[GameEntity[Entity]], GameSeq] = GameSeqImpl(_)

  /** Defines a view over the model entities, designed for the [[controller.GameLoopActor.GameLoop]].
   *
   * @param ref the [[ActorRef]] of the [[entity]]'s actor.
   * @param entity the model subject.
   * @tparam E the type of [[entity]].
   */
  case class GameEntity[E <: Entity](ref: ActorRef[ModelMessage], entity: E)

  object GameSeq:
    trait GameSeq:
      def seq: Seq[GameEntity[Entity]]
      def of[A <: Entity : GameSelectorBuilder]: Seq[GameEntity[A]] =
        seq.collect(summon[GameSelectorBuilder[A]].by)

      @targetName("delete")
      def :-(ref: ActorRef[ModelMessage]): Seq[GameEntity[Entity]] = seq filter { _.ref != ref }


      def updateWith(entity: GameEntity[Entity]): Seq[GameEntity[Entity]] =
        seq collect { case x if x.ref == entity.ref => entity case x => x }

    case class GameSeqImpl(seq: Seq[GameEntity[Entity]]) extends GameSeq

  object GameSelector:
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






