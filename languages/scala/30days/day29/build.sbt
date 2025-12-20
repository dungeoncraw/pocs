ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .enablePlugins(GraalVMNativeImagePlugin)
  .settings(
    name := "day29",
    idePackagePrefix := Some("com.tetokeguii.day29"),
    libraryDependencies += "com.github.scopt" %% "scopt" % "4.1.0",
    assembly / mainClass := Some("com.tetokeguii.day29.main"),
    assembly / assemblyJarName := "day29-fatjar.jar",
    Compile / mainClass := Some("com.tetokeguii.day29.main")
  )
