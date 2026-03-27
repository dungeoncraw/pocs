ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

lazy val root = (project in file("."))
  .settings(
    name := "slicklib",
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "3.6.1",
      "org.postgresql" % "postgresql" % "42.7.10",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.6.1",
      "org.slf4j" % "slf4j-nop" % "2.0.17",
      "org.scalatest" %% "scalatest" % "3.2.20" % Test,
      "com.h2database" % "h2" % "2.4.240" % Test
    )
  )
