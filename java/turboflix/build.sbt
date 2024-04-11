name := """turboflix"""
organization := "com.tnascimento"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.13"

libraryDependencies ++= Seq(
  guice,
  javaJpa,
  javaJdbc,
  "com.h2database"            % "h2"                  % "2.2.224",
  "org.hibernate"             % "hibernate-core"      % "6.4.4.Final",
  javaWs                      % "test",
  "org.awaitility"            % "awaitility"          % "4.2.1"         % "test",
  "org.assertj"               % "assertj-core"        % "3.25.3"        % "test",
  "org.mockito"               % "mockito-core"        % "5.11.0"        % "test",
  "org.webjars"               % "bootstrap"           % "5.3.3",
  "com.adrianhurt"            %%"play-bootstrap"      % "1.6.1-P28-B4",
  "org.webjars"               % "jquery"              % "3.7.1"
)

Assets / LessKeys.less / includeFilter:= "*.less"
Assets / LessKeys.less / excludeFilter := "_*.less"

Test / testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")

scalacOptions ++= List("-feature", "-Werror")
javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")

PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"