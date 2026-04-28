ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "logger",
    idePackagePrefix := Some("com.tetokeguii.logger")
  )
