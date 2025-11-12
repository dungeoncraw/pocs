ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .settings(
    name := "day13",
    idePackagePrefix := Some("com.tetokeguii.day13")
  )
