package com.tetokeguii.day28

import cats.effect.{IO, IOApp}

object CatsComposed extends IOApp.Simple:
  private def readName: IO[String] = IO("Alice")
  private def greet(name: String): IO[Unit] = IO.println(s"Hello, $name from composed IO")
  val run: IO[Unit] = for
    name <- readName
    _    <- greet(name)
  yield ()


object CatsConcurrency extends IOApp.Simple:
  val fiberProgram: IO[Unit] = for
    _ <- IO.println("starting")
    f <- IO.sleep(scala.concurrent.duration.Duration(200, "millis")).as(())
      .start
    _ <- IO.println("launched fiber")
    _ <- f.join
    _ <- IO.println("fiber finished")
  yield ()
  val run: IO[Unit] = fiberProgram
