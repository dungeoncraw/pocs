ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

val springBootVersion = "4.0.4"

lazy val root = (project in file("."))
  .settings(
    name := "csrf",
    libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter-web" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-security" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-thymeleaf" % springBootVersion
    )
  )
