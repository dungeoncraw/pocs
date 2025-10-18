package com.tetokeguii.day03

import scala.io.StdIn
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

@main
def main(): Unit = {
  val dividend = Try(StdIn.readLine("Enter an Integer dividend:\n").toInt)
  val divisor = Try(StdIn.readLine("Enter an Integer divisor:\n").toInt)
  val problem = dividend.flatMap(x => divisor.map(y => x/y))
  problem match {
    case Success(v) =>
      println(s"The result is $v")
      Success(v)
    case Failure(e) =>
      println(s"Error: ${e.getMessage}")
      Failure(e)
  }

  // try catch must pick up NonFatal errors and throw fatal errors
  try {
    throw new OutOfMemoryError("OOM")
  } catch {
    case NonFatal(e) => println(s"Non-fatal error: ${e.getMessage}") // not shown for OutOfMemoryError error
    case e: Throwable => throw e
  }
}

