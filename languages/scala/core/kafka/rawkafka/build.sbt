ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "rawkafka",
    idePackagePrefix := Some("com.tetokeguii.rawkafka"),
    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-clients" % "4.2.0"
    )
  )
