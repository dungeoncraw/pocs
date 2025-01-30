ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.3"
// TODO test https://pekko.apache.org/docs/pekko/1.1/typed/guide/modules.html#persistence

val PekkoHttpVersion = "1.1.0"
val PekkoVersion = "1.1.3"
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
  "org.apache.pekko" %% "pekko-stream" % PekkoVersion,
  "org.apache.pekko" %% "pekko-http-spray-json" % PekkoHttpVersion,
  "org.apache.pekko" %% "pekko-http" % PekkoHttpVersion
)

lazy val root = (project in file("."))
  .settings(
    name := "todos",
    idePackagePrefix := Some("com.tetokeguii")
  )
