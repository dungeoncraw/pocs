ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

lazy val root = (project in file("."))
  .settings(
    name := "pekkolib",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % "1.4.0",
      "org.apache.pekko" %% "pekko-stream" % "1.4.0",
      "org.apache.pekko" %% "pekko-http" % "1.3.0",
      "org.apache.pekko" %% "pekko-http-testkit" % "1.3.0" % Test,
      "org.apache.pekko" %% "pekko-testkit" % "1.4.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )
  )
