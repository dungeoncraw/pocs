ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

lazy val root = (project in file("."))
  .settings(
    name := "postprocessor",
    libraryDependencies ++= Seq(
      "org.springframework" % "spring-context" % "7.0.5"
    )
  )
