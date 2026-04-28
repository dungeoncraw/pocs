ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "taxSystem",
    idePackagePrefix := Some("com.tetokeguii.taxsystem"),
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % "1.5.0",
      "org.apache.pekko" %% "pekko-stream" % "1.5.0",
      "org.apache.pekko" %% "pekko-http" % "1.3.0",
      "org.apache.pekko" %% "pekko-http-spray-json" % "1.3.0",
      "com.typesafe.slick" %% "slick" % "3.6.1",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.6.1",
      "org.postgresql" % "postgresql" % "42.7.10",
      "ch.qos.logback" % "logback-classic" % "1.5.32",
      "org.scalatest" %% "scalatest" % "3.2.20" % Test,
      "org.apache.pekko" %% "pekko-http-testkit" % "1.3.0" % Test,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % "1.5.0" % Test
    ),
    dockerBaseImage := "eclipse-temurin:21-jre",
    dockerExposedPorts := Seq(8080),
    executableScriptName := "tax-system"
  )
