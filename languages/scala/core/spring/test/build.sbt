ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

val springBootVersion = "3.4.3"

lazy val root = (project in file("."))
  .settings(
    name := "spring-scala-test",
    libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter-web" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-test" % springBootVersion % Test,
      "org.springframework.boot" % "spring-boot-starter-webflux" % springBootVersion % Test,
      "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.18.2"
    )
  )
