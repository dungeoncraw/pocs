ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "grocery",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.3.0" % Test
    )
  )
