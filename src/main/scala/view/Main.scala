package view

import com.badlogic.gdx.backends.lwjgl3.*

object Main extends App :
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("PVZ")
    config.setResizable(true)
    config.setWindowedMode(960, 540)
    Lwjgl3Application(Game, config)