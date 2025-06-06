ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.jooq" %% "jooq-scala" % "3.20.4",
  "com.h2database" % "h2" % "2.3.232",
)

lazy val root = (project in file("."))
  .settings(
    name := "jooqpoc",
    idePackagePrefix := Some("com.tetokeguii.jooq")
  )
