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

val akkaVersion = "2.6.20"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test, // sbt's test interface for JUnit 4
  "org.junit.jupiter" % "junit-jupiter" % "5.9.0" % Test, // aggregator of junit-jupiter-api and junit-jupiter-engine (runtime)
  "org.junit.jupiter" % "junit-jupiter-engine" % "5.9.0" % Test, // for org.junit.platform
  "org.junit.vintage" % "junit-vintage-engine" % "5.9.0" % Test,
  "org.junit.platform" % "junit-platform-launcher" % "1.9.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.12" % Test
)