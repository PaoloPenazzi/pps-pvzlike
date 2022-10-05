package controller

import model.entities.WorldSpace.given
import model.entities.{Enemy, Zombie}

object WaveSupervisor:
  // TODO create a Wave Model and then use Prolog Engine to make all wave
  def generateWave(enemiesNumber: Int): List[Enemy] = List.fill(enemiesNumber)(Zombie((1, 100)))
