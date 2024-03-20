name := """turboflix"""
organization := "com.tnascimento"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.13"

libraryDependencies += guice

Assets / LessKeys.less / includeFilter:= "*.less"
Assets / LessKeys.less / excludeFilter := "_*.less"