ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "zioserver",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.1.25",
      "dev.zio" %% "zio-http" % "3.10.1"
    )
  )
