ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .settings(
    name := "day20",
    idePackagePrefix := Some("com.tetokeguii.day20"),
    libraryDependencies ++=  Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.scalameta" %% "munit" % "1.2.1" % Test
    )
  )
