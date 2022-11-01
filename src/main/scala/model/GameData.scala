package model

import akka.actor.typed.ActorRef
import model.GameData.GameSeq.GameSeqBuilder
import model.actors.ModelMessage
import model.entities.{Bullet, Enemy, Entity, PeaShooter, Plant, Zombie}

object GameData :
  
  case class GameEntity[T, E <: Entity](ref: ActorRef[T], entity: E)


  object GameSeq:
    trait GameSeqBuilder[T, E <: Entity]:
      def of(seq: Seq[GameEntity[T, Entity]]) : Seq[GameEntity[T, E]]

    given GameSeqBuilder[ModelMessage, Plant] with
      override def of(seq: Seq[GameEntity[ModelMessage, Entity]]): Seq[GameEntity[ModelMessage, Plant]] =
        seq.collect{ case e if e.entity.isInstanceOf[Plant] => GameEntity(e.ref, e.entity.asInstanceOf[Plant]) }

    given GameSeqBuilder[ModelMessage, Bullet] with
      override def of(seq: Seq[GameEntity[ModelMessage, Entity]]): Seq[GameEntity[ModelMessage, Bullet]] =
        seq.collect { case e if e.entity.isInstanceOf[Bullet] => GameEntity(e.ref, e.entity.asInstanceOf[Bullet]) }

    given GameSeqBuilder[ModelMessage, Enemy] with
      override def of(seq: Seq[GameEntity[ModelMessage, Entity]]): Seq[GameEntity[ModelMessage, Enemy]] =
        seq.collect { case e if e.entity.isInstanceOf[Enemy] => GameEntity(e.ref, e.entity.asInstanceOf[Enemy]) }

  case class GameSeq[T](seq: Seq[GameEntity[T, Entity]]):

    def ofType[A <: Entity](using gameSeqBuilder: GameSeqBuilder[T, A]): Seq[GameEntity[T, A]] = gameSeqBuilder of seq





