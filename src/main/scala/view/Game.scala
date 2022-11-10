package view

import akka.actor.typed.ActorSystem
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{Game, Gdx, ScreenAdapter}
import controller.actors.GameLoopActor.GameLoopCommands.Command
import controller.actors.RootActor
import controller.actors.RootActor.RootCommands.{RootCommand, Start}
import model.Statistics.GameStatistics
import scalagdx.Screen.*
import ViewportSpace.{ViewportWidth, ViewportHeight}



object Game extends com.badlogic.gdx.Game :
  val viewport: Viewport = FitViewport(ViewportWidth, ViewportHeight)
  var actorSystem: Option[ActorSystem[RootCommand]] = None

  def startNewGame(): Unit =
    actorSystem = Some(ActorSystem(RootActor(), "launcher"))
    actorSystem.foreach(_ ! Start())

  def changeScreen(behavior: ScreenBehavior): Unit =
    Gdx.app.postRunnable(() =>
        setScreen(BasicScreen(behavior))
    )

  def endGame(stats: GameStatistics): Unit =
    Gdx.app.postRunnable(() =>
        terminateActorSystem()
        changeScreen(GameOverScreen(stats))
    )

  private def terminateActorSystem(): Unit =
    actorSystem.foreach(_.terminate())

  override def create(): Unit =
    changeScreen(MainMenuScreen())

  override def dispose(): Unit =
    super.dispose()
    terminateActorSystem()
