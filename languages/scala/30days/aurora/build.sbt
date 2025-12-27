ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .settings(
    name := "aurora",
    idePackagePrefix := Some("com.tetokeguii.aurora"),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "mainargs" % "0.7.6",
      "com.lihaoyi" %% "upickle" % "4.0.2",
      "com.lihaoyi" %% "os-lib" % "0.11.3",
      "org.scalameta" %% "munit" % "1.0.0" % Test
    )
  )
