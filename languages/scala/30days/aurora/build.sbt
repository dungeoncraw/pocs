ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .settings(
    name := "aurora",
    idePackagePrefix := Some("com.tetokeguii.aurora"),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "mainargs" % "0.7.6",
      "com.lihaoyi" %% "upickle" % "3.1.0",
      "com.lihaoyi" %% "os-lib" % "0.11.3",
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.lihaoyi" %% "cask" % "0.9.4",
      "org.commonmark" % "commonmark" % "0.24.0",
      "org.commonmark" % "commonmark-ext-gfm-tables" % "0.24.0",
      "org.scalameta" %% "munit" % "1.0.0" % Test
    )
  )
