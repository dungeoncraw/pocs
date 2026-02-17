ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.1"

lazy val root = (project in file("."))
  .settings(
    name := "currying",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test
  )
