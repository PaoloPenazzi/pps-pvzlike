package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

trait Controller:
  def standardBehavior: Behavior[Command]

trait PauseAbility extends Controller:
  def pauseBehavior: Behavior[Command]