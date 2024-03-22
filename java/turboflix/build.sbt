name := """turboflix"""
organization := "com.tnascimento"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.13"

libraryDependencies += guice


libraryDependencies ++= Seq(
  "org.webjars"               % "bootstrap"           % "3.3.4",
  "com.adrianhurt"            %%"play-bootstrap"      % "1.6.1-P28-B4",
  "org.webjars"               % "jquery"              % "1.8.3"
)

Assets / LessKeys.less / includeFilter:= "*.less"
Assets / LessKeys.less / excludeFilter := "_*.less"