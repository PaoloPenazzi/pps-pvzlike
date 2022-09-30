package controller

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import akka.actor.typed.scaladsl.adapter.*
import model.entities.Turrets.Turret
import model.entities.{Bullet, Enemy, Entity}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

object GameLoopActor:

  object GameLoopCommands:
    sealed trait GameLoopCommand extends Command

    case class StartLoop() extends GameLoopCommand

    case class Update() extends GameLoopCommand

    case class Start[E <: Enemy](wave: List[E]) extends GameLoopCommand

    case class Pause() extends GameLoopCommand

    case class Resume() extends GameLoopCommand

    case class StartGame() extends GameLoopCommand

    case class FinishGame() extends GameLoopCommand

    case class NewTurretAdded[T <: Turret](turret: T) extends GameLoopCommand

    case class NewEnemiesWave[E <: Enemy](wave: List[E]) extends GameLoopCommand

    // the presence or not of an entity is defined by this message: if i receive i will update that entity otherwise the
    // entity is dead and so i don't have to update that one
    case class EntityUpdate[E <: Entity](entity: E) extends GameLoopCommand

    case class Stop() extends GameLoopCommand

  // TODO from here, make it better...
  var enemiesWave: Seq[(ActorRef[Enemy], Enemy)] = List[(ActorRef[Enemy], Enemy)]()
  var bullets: Seq[(ActorRef[Bullet], Bullet)] = List[(ActorRef[Bullet], Bullet)]()
  var entities: Seq[(ActorRef[Entity], Entity)] = List[(ActorRef[Entity], Entity)]()

  def apply(): Behavior[Command] =
    Behaviors.setup { _ => Behaviors.withTimers { timer => GameLoopActor(timer).standardBehavior() } }

  import GameLoopCommands.*

  private case class GameLoopActor(timer: TimerScheduler[Command]) extends Controller with PausableController:
    override def standardBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case StartLoop() =>
          createWave(ctx)
          startTimer(timer)
          Behaviors.same
        case Update() =>
          // todo check if the wave is end
          detectCollision
          updateAll(detectInterest)
          startTimer(timer)
          Behaviors.same
        case Start(wave) =>
          // model ! start
          // view ! start
          // enemiesWave = Some(wave)
          startTimer(timer)
          Behaviors.same
        case EntityUpdate(entity) =>
          // pass the model to view
          Behaviors.same
        case Pause() => pauseBehavior()
        case Stop() => Behaviors.stopped
        case _ => Behaviors.same
    })

    override def pauseBehavior(): Behavior[Command] = Behaviors.receive((ctx, msg) => {
      msg match
        case Stop() => Behaviors.stopped
        case Resume() =>
          ctx.self ! Update()
          standardBehavior()
        case _ => Behaviors.same
    })

    def startTimer(timer: TimerScheduler[Command]) = timer.startSingleTimer(Update(), FiniteDuration(10, "second"))

    def createWave(ctx: ActorContext[Command]) = ???
      // genera una wave di zombie poi la lista di zombie creata verrà mappata in un'altra lista fatta di tuple (zombieRef, zombie)
      // e alla fine gli zombie verranno aggiunti alla lista enemiesWave lista che contiene tutti gli zombie
      // WaveSupervisor.generateWave(2).map( e => (ctx.spawnAnonymous(EnemyActor(enemy)), e)).foreach(t => enemiesWave = enemiesWave + t)

    def detectCollision = ???
      // todo provarlo a fare con for-yield o pimp-my-library
      // controlla per ogni bullet nel gioco se questo ha colpito qualche entità (che non sia lui). Se il bullet ha colpito qualcuno (quindi
      // la lista entitiesColl non è vuota) allora mando un messaggio di collisione ad ogni entità e al bullet in questione
       /*bullets.foreach(b =>
         val entitiesColl = entities.filter(e => (b._1 != e._1) && b._2.collideWith(e._2))
         if entitiesColl.nonEmpty then entities.foreach(e => {e._1 ! CollisionWith(b._2); b._1 ! CollisionWith(e._2)}))*/


    def detectInterest: Seq[(ActorRef[Entity], Seq[Entity])] =  ???
    // crea una lista contenente (riferimento all entità, lista di entità a cui è interessato)
      /*for
        e1 <- entities
      yield
        (e1._1, for
          e2 <- entities
          if e1._1 != e2._1
          if e1._2.filter(e2._2)
        yield e2._2)*/

    def updateAll(interestForAll: Seq[(ActorRef[Entity], Seq[Entity])]) = ???
      // mando un messaggio di update ad ogni entità e gli allego le entità a cui è interessato
      // interestForAll.foreach(e => e._1 ! UpdateModel(e._2.map( x => x._2)))







  
  


