package model

import akka.actor.typed.ActorRef
import model.GameData.GameSelector.GameSelectorBuilder
import model.actors.ModelMessage
import model.entities.{Bullet, Entity, PeaShooter, Plant, Zombie}

object GameData :

  case class GameEntity[E <: Entity](ref: ActorRef[ModelMessage], entity: E)

  case class GameSeq(seq: Seq[GameEntity[Entity]]):
    def ofType[A <: Entity](using select: GameSelectorBuilder[A]): Seq[GameEntity[A]] =
      seq.collect(select.of)

  object GameSelector:
    trait GameSelectorBuilder[E <: Entity]:
      def of: PartialFunction[GameEntity[Entity], GameEntity[E]]

    given GameSelectorBuilder[Plant] with
      override def of: PartialFunction[GameEntity[Entity], GameEntity[Plant]] =
         { case e if e.entity.isInstanceOf[Plant] => GameEntity(e.ref, e.entity.asInstanceOf[Plant]) }

    given GameSelectorBuilder[Zombie] with
      override def of: PartialFunction[GameEntity[Entity], GameEntity[Zombie]] = {
        case e if e.entity.isInstanceOf[Zombie] => GameEntity(e.ref, e.entity.asInstanceOf[Zombie])
      }

    given GameSelectorBuilder[Bullet] with
      override def of: PartialFunction[GameEntity[Entity], GameEntity[Bullet]] = {
        case e if e.entity.isInstanceOf[Bullet] => GameEntity(e.ref, e.entity.asInstanceOf[Bullet])
      }






