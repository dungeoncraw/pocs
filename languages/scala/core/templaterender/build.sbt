ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "templaterender",
    idePackagePrefix := Some("com.tetokeguii.templaterender"),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "4.4.3",
      "com.lihaoyi" %% "scalatags" % "0.13.1",
      "com.openhtmltopdf" % "openhtmltopdf-pdfbox" % "1.0.10",
      "com.nrinaudo" %% "kantan.csv" % "0.7.0" cross CrossVersion.for3Use2_13,
      "com.nrinaudo" %% "kantan.csv-generic" % "0.7.0" cross CrossVersion.for3Use2_13,
      "org.scalatest" %% "scalatest" % "3.2.20" % Test
    )
  )
