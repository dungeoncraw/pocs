ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .settings(
    name := "day27",

    // Source directories - includes both Scala and Java
    Compile / sourceDirectories += baseDirectory.value / "src" / "main" / "java",

    libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter-web" % "4.0.0",
      "org.springframework.boot" % "spring-boot-starter-validation" % "4.0.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.20.1",
      "jakarta.validation" % "jakarta.validation-api" % "3.1.1",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.20.1"
    ),

    Compile / mainClass := Some("com.example.jokes.JokesApplication"),
    javacOptions ++= Seq("--release", "21")
  )