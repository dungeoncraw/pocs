ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.2"

lazy val root = (project in file("."))
  .settings(
    name := "jdbc",
    libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter" % "4.0.3",
      "org.springframework.boot" % "spring-boot-starter-web" % "4.0.3",
      "org.springframework.boot" % "spring-boot-starter-data-jdbc" % "4.0.3",
      "org.springframework.boot" % "spring-boot-starter-data-jpa" % "4.0.3",
      "org.postgresql" % "postgresql" % "42.7.10",
      "com.google.code.gson" % "gson" % "2.13.2",
      "io.github.cdimascio" % "dotenv-java" % "3.2.0",
      "com.github.ben-manes.caffeine" % "caffeine" % "3.2.3",
      "org.springframework.boot" % "spring-boot-starter-cache" % "4.0.3",
      "org.springframework.boot" % "spring-boot-starter-test" % "4.0.3" % Test,
      "org.scalatestplus" %% "mockito-5-12" % "3.2.19.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "com.h2database" % "h2" % "2.3.232" % Test
    ),
  )
