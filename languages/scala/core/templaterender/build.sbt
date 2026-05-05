ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "templaterender",
    idePackagePrefix := Some("com.tetokeguii.templaterender"),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "4.4.3"
    )
  )
