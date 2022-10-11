package model.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import controller.GameLoopActor.GameLoopCommands.EntitySpawned
import model.entities.*

object TroopActor:
  def apply(troop: Troop): Behavior[ModelMessage] =
    standardBehaviour(troop)

  def standardBehaviour(troop: Troop): Behavior[ModelMessage] =
    Behaviors.withTimers(timer => {
      Behaviors.receive((ctx, msg) => {
        msg match
          case Update(_, entities, replyTo) => ???

          case Shoot(replyTo) => ???

          case Collision(entity, replyTo) => ???

          case _ => Behaviors.same
      })
    })

