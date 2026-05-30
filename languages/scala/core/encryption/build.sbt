ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "encryption",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.3.1" % Test
    )
  )
