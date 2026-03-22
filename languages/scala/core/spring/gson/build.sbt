ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

lazy val root = (project in file("."))
  .settings(
    name := "gson",
    libraryDependencies += "com.google.code.gson" % "gson" % "2.13.2"
  )
