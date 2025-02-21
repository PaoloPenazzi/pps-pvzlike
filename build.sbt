import sbtassembly.AssemblyPlugin.assemblySettings

assemblySettings

name := "pps-pvzlike"

version := "0.1"

assembly / assemblyMergeStrategy := {
  case "reference.conf" => MergeStrategy.concat
  case PathList("META-INF", _@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8",
  "-feature"
)

ThisBuild / scalaVersion := "3.6.3"

lazy val root = (project in file("."))
  .settings(
    name := "pps-pvzlike",
    assembly / assemblyJarName := "pps-pvzlike.jar",
    libraryDependencies ++= Dependencies.Gdx()
  )


val akkaVersion = "2.8.8"

libraryDependencies ++= Seq(
  "it.unibo.alice.tuprolog" % "2p-core" % "4.1.1",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test, // sbt's test interface for JUnit 4
  "org.junit.jupiter" % "junit-jupiter" % "5.12.0" % Test, // aggregator of junit-jupiter-api and junit-jupiter-engine (runtime)
  "org.junit.jupiter" % "junit-jupiter-engine" % "5.12.0" % Test, // for org.junit.platform
  "org.junit.vintage" % "junit-vintage-engine" % "5.12.0" % Test,
  "org.junit.platform" % "junit-platform-launcher" % "1.12.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)