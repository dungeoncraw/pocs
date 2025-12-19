ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .settings(
    name := "day28",
    idePackagePrefix := Some("com.tetokeguii.day28"),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.6.3",
      "org.typelevel" %% "cats-core" % "2.13.0",
      "dev.zio" %% "zio" % "2.1.23"
    )
  )
