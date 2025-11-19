package com.tetokeguii.day17

import scala.concurrent.*
import scala.concurrent.duration.*
import scala.util.{Failure, Success}
import ExecutionContext.Implicits.global
import java.util.concurrent.TimeoutException

@main
def main(): Unit =
  //  simulate downloading resources
  def fetchResource(name: String, delay: FiniteDuration): Future[String] =
    Future {
      println(s"[$name] starting download...")
      Thread.sleep(delay.toMillis)
      println(s"[$name] download finished.")
      s"content-$name"
    }

  val combined: Future[(String, String)] =
    val fetch1 = fetchResource("resource-1", 800.millis)
    val fetch2 = fetchResource("resource-2", 1200.millis)

    val both = for
      result1 <- fetch1
      result2 <- fetch2
    yield (result1, result2)
    Future.firstCompletedOf(Seq(both))

  combined.onComplete {
    case Success((r1, r2)) =>
      println(s"Results: $r1 | $r2")
    case Failure(e) =>
      println(s"Error: ${e.getClass.getSimpleName}: ${e.getMessage}")
  }

  try
    val result = Await.result(combined, 2.seconds)
    println(s"Await.result => $result")
  catch
    case e: TimeoutException =>
      println(s"Await timeout: ${e.getMessage}")