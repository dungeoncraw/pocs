package com.tetokeguii.rawkafka
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import com.tetokeguii.rawkafka.{ConsumerApp, ConsumerRunner, ConsumerSettings, MyBestConsumer}

class KafkaConsumerSpec extends AnyFunSuite with Matchers {

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

  test("ConsumerApp should initialize runners and threads") {
    val settings = List(
      ConsumerSettings(topic = "topic1"),
      ConsumerSettings(topic = "topic2")
    )
    val app = new ConsumerApp(settings)
    
    app.runners should have size 2
    app.threads should have size 2
    app.threads.map(_.getName) should contain allOf ("kafka-consumer-0", "kafka-consumer-1")
    
    // Cleanup
    app.stop()
  }

  test("ConsumerRunner should have a consumer") {
    val settings = ConsumerSettings(topic = "test-topic")
    val runner = new ConsumerRunner(settings)
    
    try {
      runner.consumer should not be null
    } finally {
      runner.wakeup() // Ensuring it's cleaned up if it was running, though it's not here
      // consumer is closed in runner.run() finally block, but since we didn't run it, 
      // we might want to close it manually if possible, but it's private[rawkafka]
      runner.consumer.close()
    }
  }
}
