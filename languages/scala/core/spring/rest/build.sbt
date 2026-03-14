ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

val springBootVersion = "3.4.3"

lazy val root = (project in file("."))
  .settings(
    name := "rest",
    libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter-web" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-test" % springBootVersion % Test,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.18.2"
    )
  )
