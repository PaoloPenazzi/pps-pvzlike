package model

import akka.actor.typed.ActorRef
// import model.GameData.GameSeq.GameSeqBuilder
import model.GameData.GameSelector.GameSelectorBuilder
import model.actors.ModelMessage
import model.entities.{Bullet, Entity, PeaShooter, Plant, Zombie}

object GameData :

  case class GameEntity[T, E <: Entity](ref: ActorRef[T], entity: E)

  case class GameSeq[T](seq: Seq[GameEntity[T, Entity]]):
    def ofType[A <: Entity](using select: GameSelectorBuilder[T, A]): Seq[GameEntity[T, A]] =
      seq.collect(select.of)

  object GameSelector:
    trait GameSelectorBuilder[T, E <: Entity]:
      def of: PartialFunction[GameEntity[T, Entity], GameEntity[T, E]]

    given GameSelectorBuilder[ModelMessage, Plant] with
      override def of: PartialFunction[GameEntity[ModelMessage, Entity], GameEntity[ModelMessage, Plant]] =
         { case e if e.entity.isInstanceOf[Plant] => GameEntity(e.ref, e.entity.asInstanceOf[Plant]) }

    given GameSelectorBuilder[ModelMessage, Zombie] with
      override def of: PartialFunction[GameEntity[ModelMessage, Entity], GameEntity[ModelMessage, Zombie]] = {
        case e if e.entity.isInstanceOf[Zombie] => GameEntity(e.ref, e.entity.asInstanceOf[Zombie])
      }

    given GameSelectorBuilder[ModelMessage, Bullet] with
      override def of: PartialFunction[GameEntity[ModelMessage, Entity], GameEntity[ModelMessage, Bullet]] = {
        case e if e.entity.isInstanceOf[Bullet] => GameEntity(e.ref, e.entity.asInstanceOf[Bullet])
      }






