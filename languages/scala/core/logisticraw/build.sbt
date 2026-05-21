ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "logisticraw",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % Test
  )
