ThisBuild / scalaVersion := "3.8.3"
val typesafeConfigVersion = "1.4.3"

lazy val root = (project in file("."))
  .settings(
    name := "logger",
    idePackagePrefix := Some("com.tetokeguii.logger"),
    libraryDependencies += "com.typesafe" % "config" % "1.4.3"
  )
