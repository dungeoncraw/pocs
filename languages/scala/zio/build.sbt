val scala3Version = "3.5.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Scala 3 Project Template",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "dev.zio"      %% "zio" % "2.1.11",
      "dev.zio"      %% "zio-streams" % "2.1.11",
      "dev.zio"      %% "zio-http" % "3.0.1",
    )

  )
