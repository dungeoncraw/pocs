ThisBuild / scalaVersion := "3.8.4"

lazy val root = (project in file("."))
  .settings(
    name := "notesystem",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "4.4.3",
      "com.lihaoyi" %% "os-lib" % "0.11.8",
      "org.scalameta" %% "munit" % "1.3.2" % Test
    )
  )
