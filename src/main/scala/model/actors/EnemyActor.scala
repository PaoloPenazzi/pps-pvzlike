package model.actors

object EnemyActor:
  enum EnemyActorCommand:
    case ComputeVelocityRequest(entities: mutable.Seq[Enemy], replyTo: ActorRef[GameLoopCommand])
    case ComputePositionRequest(boundary: Boundary, replyTo: ActorRef[GameLoopCommand])
  export EnemyActorCommand.*
  def apply(enemy: Enemy): Behavior[EnemyActorCommand] = Behaviors.setup(new EnemyActor(_, enemy))

class BodyActor(context: ActorContext[BodyActor.BodyActorCommand], val enemy: Enemy)
  extends AbstractBehavior[EnemyActor.EnemyActorCommand](context):
  override def onMessage(msg: EnemyActor.EnemyActorCommand): Behavior[EnemyActor.EnemyActorCommand] = msg match {
    case ComputeVelocityRequest(entities, ref) =>
      enemy.computeBodyVelocity(entities)
      ref ! ComputeVelocityResponse(enemy)
      Behaviors.same
    case ComputePositionRequest(boundary, ref) =>
      enemy.computeBodyPosition(boundary)
      ref ! ComputePositionResponse(enemy)
      Behaviors.same
    case _ => Behaviors.same
  }
