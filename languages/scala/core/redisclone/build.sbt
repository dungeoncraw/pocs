ThisBuild / scalaVersion := "3.8.4"

lazy val root = (project in file("."))
  .settings(
    name := "redisclone",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test
  )
