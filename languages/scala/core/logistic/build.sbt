ThisBuild / scalaVersion := "3.8.3"

val springBootVersion = "4.0.6"

lazy val root = (project in file("."))
  .settings(
    name := "logistic",
    libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter-web" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-validation" % springBootVersion,
      "org.springframework.boot" % "spring-boot-starter-jdbc" % springBootVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.21.3",
      "org.postgresql" % "postgresql" % "42.7.11",
      "org.springframework.boot" % "spring-boot-starter-test" % springBootVersion % Test,
      "com.h2database" % "h2" % "2.4.240" % Test,
      "org.springframework.boot" % "spring-boot-starter-test" % springBootVersion % Test
    )
  )
