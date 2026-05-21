package com.tetokeguii.rawkafka

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import java.util.Properties
import scala.annotation.tailrec

final case class ConsumerSettings(
                                   topic: String = "test-topic",
                                   bootstrapServers: String = "localhost:9092",
                                   groupId: String = "scala-consumer",
                                   enableAutoCommit: Boolean = false
                                 )

object MyBestConsumer {
  def settingsFromArgs(args: Seq[String]): ConsumerSettings = {
    @tailrec
    def parse(args: List[String], settings: ConsumerSettings): ConsumerSettings = args match {
      case Nil => settings
      case "--topic" :: value :: tail => parse(tail, settings.copy(topic = value))
      case "--bootstrap-servers" :: value :: tail => parse(tail, settings.copy(bootstrapServers = value))
      case "--group-id" :: value :: tail => parse(tail, settings.copy(groupId = value))
      case "--enable-auto-commit" :: value :: tail => parse(tail, settings.copy(enableAutoCommit = value.toBoolean))
      case unknown :: tail =>
        println(s"Warning: Unknown argument $unknown")
        parse(tail, settings)
    }

    if (args.isEmpty) ConsumerSettings()
    else if (args.head.startsWith("-")) parse(args.toList, ConsumerSettings())
    else {
      // Fallback for legacy positional arguments if first arg doesn't look like an option
      ConsumerSettings(
        topic = args.headOption.getOrElse("test-topic"),
        bootstrapServers = args.lift(1).getOrElse("localhost:9092"),
        groupId = args.lift(2).getOrElse("scala-consumer"),
        enableAutoCommit = args.lift(3).exists(_.toBoolean)
      )
    }
  }

  def fromSettings(settings: ConsumerSettings): MyBestConsumer =
    MyBestConsumer(settings.bootstrapServers, settings.groupId, settings.enableAutoCommit)
}

class MyBestConsumer(
                      bootstrapServers: String = "localhost:9092",
                      groupId: String = "scala-consumer",
                      enableAutoCommit: Boolean = false
                    ) {

  def buildProperties(): Properties = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit.toString)
    props
  }

  def build(): KafkaConsumer[String, String] = {
    val props = buildProperties()

    println(s"Bootstrap servers: $bootstrapServers")
    println(s"Consumer group: $groupId")
    println(s"Enable auto commit: $enableAutoCommit")

    new KafkaConsumer[String, String](props)
  }
}