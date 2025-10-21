ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.3"

lazy val root = (project in file("."))
  .settings(
    name := "day06",
    idePackagePrefix := Some("org.tetokeguii.day06")
  )
