package controller

import model.entities.{Enemy, Zombie}
import model.entities.WorldSpace.given

object WaveSupervisor :
  // TODO create a Wave Model and then use Prolog Engine to make all wave
  def generateWave(enemiesNumber: Int): List[Enemy] = List.fill(enemiesNumber)(Zombie((0,0)))
