ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

lazy val root = (project in file("."))
  .settings(
    name := "catslib",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.13.0",
      "org.typelevel" %% "cats-effect" % "3.7.0",
      "org.http4s" %% "http4s-ember-server" % "0.23.33",
      "org.http4s" %% "http4s-dsl" % "0.23.33",
      "org.http4s" %% "http4s-circe" % "0.23.33",
      "io.circe" %% "circe-generic" % "0.14.10",
      "io.circe" %% "circe-parser" % "0.14.10",
      "org.typelevel" %% "munit-cats-effect" % "2.0.0" % Test
    )
  )
