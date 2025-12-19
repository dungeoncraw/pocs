package com.tetokeguii.day28

import zio._

object ZioComposed extends ZIOAppDefault:
  val readName: UIO[String] = ZIO.succeed("Bob")
  def greet(name: String) = Console.printLine(s"Hello, $name from composed")
  def run = for
    name <- readName
    _    <- greet(name)
  yield ()

object ZioConcurrency extends ZIOAppDefault:
  def run = for
    _ <- Console.printLine("starting")
    // this creates a sleep and *> just ignore the sleep result and execute the printLine
    // fork creates a fiber and returns a fiber reference
    f <- (ZIO.sleep(200.millis) *> Console.printLine("fiber work")).fork
    _ <- Console.printLine("launched fiber")
    // blocks the execution until the fiber finishes
    _ <- f.join
    _ <- Console.printLine("fiber finished")
    result <- ZIO.succeed("Concurrency completed successfully")
    _ <- Console.printLine(result)
  yield result