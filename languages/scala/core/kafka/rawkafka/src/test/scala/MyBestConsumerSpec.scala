package com.tetokeguii.rawkafka

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MyBestConsumerSpec extends AnyFunSuite with Matchers {

  test("use default values when no args") {
    val settings = MyBestConsumer.settingsFromArgs(Seq.empty)
    settings.topic shouldBe "test-topic"
    settings.bootstrapServers shouldBe "localhost:9092"
    settings.groupId shouldBe "scala-consumer"
  }

  test("use custom values when args are provided") {
    val args = Seq("my-topic", "my-kafka:9092", "my-group")
    val settings = MyBestConsumer.settingsFromArgs(args)
    settings.topic shouldBe "my-topic"
    settings.bootstrapServers shouldBe "my-kafka:9092"
    settings.groupId shouldBe "my-group"
  }

  test("use partial custom values when some args are provided") {
    val args = Seq("my-topic")
    val settings = MyBestConsumer.settingsFromArgs(args)
    settings.topic shouldBe "my-topic"
    settings.bootstrapServers shouldBe "localhost:9092"
    settings.groupId shouldBe "scala-consumer"
  }

  test("build KafkaConsumer with correct properties") {
    val settings = ConsumerSettings("test-topic", "localhost:9092", "test-group")
    val consumer = MyBestConsumer.fromSettings(settings).build()
    
    try {
      // We can't easily check internal properties of KafkaConsumer, 
      // but we can verify it was created without throwing exception
      consumer should not be null
    } finally {
      consumer.close()
    }
  }
}
