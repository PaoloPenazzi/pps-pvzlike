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

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "pps-pvzlike",
    assembly / assemblyJarName := "pps-pvzlike.jar",
    libraryDependencies ++= Dependencies.Gdx()
  )


val akkaVersion = "2.8.5"

libraryDependencies ++= Seq(
  "it.unibo.alice.tuprolog" % "2p-core" % "4.1.1",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test, // sbt's test interface for JUnit 4
  "org.junit.jupiter" % "junit-jupiter" % "5.10.1" % Test, // aggregator of junit-jupiter-api and junit-jupiter-engine (runtime)
  "org.junit.jupiter" % "junit-jupiter-engine" % "5.10.1" % Test, // for org.junit.platform
  "org.junit.vintage" % "junit-vintage-engine" % "5.10.1" % Test,
  "org.junit.platform" % "junit-platform-launcher" % "1.10.1" % Test,
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)