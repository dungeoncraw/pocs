package com.tetokeguii.rawkafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MyBestConsumerSpec extends AnyFunSuite with Matchers {

  test("use default values when no args") {
    val settings = MyBestConsumer.settingsFromArgs(Seq.empty)
    settings.topic shouldBe "test-topic"
    settings.bootstrapServers shouldBe "localhost:9092"
    settings.groupId shouldBe "scala-consumer"
    settings.enableAutoCommit shouldBe false
  }

  test("use custom values when args are provided") {
    val args = Seq("my-topic", "my-kafka:9092", "my-group", "true")
    val settings = MyBestConsumer.settingsFromArgs(args)
    settings.topic shouldBe "my-topic"
    settings.bootstrapServers shouldBe "my-kafka:9092"
    settings.groupId shouldBe "my-group"
    settings.enableAutoCommit shouldBe true
  }

  test("use partial custom values when some args are provided") {
    val args = Seq("my-topic")
    val settings = MyBestConsumer.settingsFromArgs(args)
    settings.topic shouldBe "my-topic"
    settings.bootstrapServers shouldBe "localhost:9092"
    settings.groupId shouldBe "scala-consumer"
    settings.enableAutoCommit shouldBe false
  }

  test("use named arguments") {
    val args = Seq("--topic", "named-topic", "--enable-auto-commit", "true")
    val settings = MyBestConsumer.settingsFromArgs(args)
    settings.topic shouldBe "named-topic"
    settings.bootstrapServers shouldBe "localhost:9092"
    settings.groupId shouldBe "scala-consumer"
    settings.enableAutoCommit shouldBe true
  }

  test("use named arguments in different order") {
    val args = Seq("--group-id", "custom-group", "--bootstrap-servers", "kafka:9093")
    val settings = MyBestConsumer.settingsFromArgs(args)
    settings.topic shouldBe "test-topic"
    settings.bootstrapServers shouldBe "kafka:9093"
    settings.groupId shouldBe "custom-group"
    settings.enableAutoCommit shouldBe false
  }

  test("build properties with correct values") {
    val settings = ConsumerSettings("test-topic", "localhost:9092", "test-group", enableAutoCommit = true)
    val props = MyBestConsumer.fromSettings(settings).buildProperties()

    props.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG) shouldBe "localhost:9092"
    props.getProperty(ConsumerConfig.GROUP_ID_CONFIG) shouldBe "test-group"
    props.getProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG) shouldBe "true"
    props.getProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG) shouldBe "earliest"
  }

  test("build KafkaConsumer with correct properties") {
    val settings = ConsumerSettings("test-topic", "localhost:9092", "test-group")
    val consumer = MyBestConsumer.fromSettings(settings).build()
    
    try {
      consumer should not be null
    } finally {
      consumer.close()
    }
  }
}
