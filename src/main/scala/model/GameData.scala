package model

import akka.actor.typed.ActorRef
import model.GameData.GameSelector.GameSelectorBuilder
import model.actors.ModelMessage
import model.entities.{Bullet, Entity, PeaShooter, Plant, Troop, Zombie}

object GameData :

  case class GameEntity[E <: Entity](ref: ActorRef[ModelMessage], entity: E)

  case class GameSeq(seq: Seq[GameEntity[Entity]]):
    def ofType[A <: Entity](using select: GameSelectorBuilder[A]): Seq[GameEntity[A]] =
      seq.collect(select.of)
  given Conversion[Seq[GameEntity[Entity]], GameSeq] = GameSeq(_)
    
  object GameSelector:
    trait GameSelectorBuilder[E <: Entity]:
      def of: PartialFunction[GameEntity[Entity], GameEntity[E]]

    given GameSelectorBuilder[Troop] with
      override def of: PartialFunction[GameEntity[Entity], GameEntity[Troop]] =
        { case e if e.entity.isInstanceOf[Troop] => GameEntity(e.ref, e.entity.asInstanceOf[Troop]) }

    given GameSelectorBuilder[Bullet] with
      override def of: PartialFunction[GameEntity[Entity], GameEntity[Bullet]] =
        { case e if e.entity.isInstanceOf[Bullet] => GameEntity(e.ref, e.entity.asInstanceOf[Bullet]) }






