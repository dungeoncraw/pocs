ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.3"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "todos",
    idePackagePrefix := Some("com.tetokeguii")
  )
