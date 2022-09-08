ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "pps-pvzlike"
  )

lazy val javaFxLibrary = for {
  module <- Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
  os <- Seq("win", "mac", "linux")
} yield "org.openjfx" % s"javafx-$module" % "15.0.1" classifier os
