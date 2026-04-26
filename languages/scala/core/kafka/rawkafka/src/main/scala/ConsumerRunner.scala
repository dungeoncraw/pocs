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

      while (true) {
        val records = consumer.poll(Duration.ofMillis(1000))

        records.asScala.foreach { record =>
          println(
            s"topic=${record.topic()}, partition=${record.partition()}, offset=${record.offset()}, key=${record.key()}, value=${record.value()}"
          )
        }
      }
    } catch {
      case _: WakeupException =>
        println(s"Consumer shutting down for topic: ${settings.topic}")
    } finally {
      consumer.close()
    }
  }
}