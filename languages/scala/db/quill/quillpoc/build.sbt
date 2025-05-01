ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.4"

libraryDependencies ++= Seq(
  "io.getquill"          %% "quill-jdbc-zio" % "4.8.5",
  "org.postgresql"       %  "postgresql"     % "42.3.1"
)

lazy val root = (project in file("."))
  .settings(
    name := "quillpoc"
  )
