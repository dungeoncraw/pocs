import sbtassembly.AssemblyPlugin.autoImport._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

lazy val root = (project in file("."))
  .settings(
    name := "jdbc",
    libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter" % "3.4.3",
      "org.springframework.boot" % "spring-boot-starter-web" % "3.4.3",
      "org.springframework.boot" % "spring-boot-starter-data-jdbc" % "3.4.3",
      "org.postgresql" % "postgresql" % "42.7.10",
      "com.google.code.gson" % "gson" % "2.13.2"
    ),
  )
