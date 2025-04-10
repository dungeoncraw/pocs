ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "com.typesafe.slick" %% "slick" % "3.6.0",
  "com.h2database" % "h2" % "2.3.232",
)

lazy val root = (project in file("."))
  .settings(
    name := "slickpoc"
  )
