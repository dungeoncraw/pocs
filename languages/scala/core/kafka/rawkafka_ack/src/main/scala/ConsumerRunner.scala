package com.tetokeguii.rawkafka

import org.apache.kafka.common.errors.WakeupException

import java.time.Duration
import scala.jdk.CollectionConverters.*

final class ConsumerRunner(settings: ConsumerSettings) {

  private[rawkafka] val consumer =
    MyBestConsumer.fromSettings(settings).build()

  def wakeup(): Unit =
    consumer.wakeup()

  def run(): Unit = {
    try {
      consumer.subscribe(List(settings.topic).asJava)
      println(s"Consuming topic: ${settings.topic}")
      println(s"ACK mode: ${settings.ackMode}")

      while (true) {
        val records = consumer.poll(Duration.ofMillis(1000))

        records.asScala.foreach { record =>
          println(
            s"topic=${record.topic()}, partition=${record.partition()}, offset=${record.offset()}, key=${record.key()}, value=${record.value()}"
          )
          process(record.value())
          if (record.value() == "crash-before-commit") {
            println("Simulating JVM crash before offset commit")
            Runtime.getRuntime.halt(1)
          }
        }
        if (!records.isEmpty) {
          settings.ackMode match {
            case AckMode.Sync =>
              consumer.commitSync()
              println(s"Sync commit completed for topic: ${settings.topic}")

            case AckMode.Async =>
              consumer.commitAsync { (offsets, exception) =>
                if (exception != null)
                  println(s"Async commit failed for topic ${settings.topic}: ${exception.getMessage}")
                else
                  println(s"Async commit completed for topic ${settings.topic}: $offsets")
              }
          }
        }
      }
    } catch {
      case _: WakeupException =>
        println(s"Consumer shutting down for topic: ${settings.topic}")
    } finally {
      try {
        consumer.commitSync()
        println(s"Final sync commit completed for topic: ${settings.topic}")
      } finally {
        consumer.close()
      }
    }
  }
  private def process(value: String): Unit = {
    println(s"Processing message: $value")

    // Simulate slow business logic
    Thread.sleep(500)
  }
}