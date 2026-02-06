ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.1"

lazy val root = (project in file("."))
  .settings(
    name := "sdk",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.10.3",
      "com.lihaoyi" %% "upickle" % "4.1.0",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )
