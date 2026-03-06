ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

lazy val root = (project in file("."))
  .settings(
    name := "setters",
    libraryDependencies ++= Seq(
      "org.springframework" % "spring-context" % "6.1.14"
    )
  )
