ThisBuild / scalaVersion := "3.8.3"
val typesafeConfigVersion = "1.4.3"

lazy val root = (project in file("."))
  .settings(
    name := "logger",
    idePackagePrefix := Some("com.tetokeguii.logger"),
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.3",
      "org.scalameta" %% "munit" % "1.0.0" % Test
    )
  )
