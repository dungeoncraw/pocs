package com.tetokeguii.rawkafka

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import java.util.Properties


enum AckMode {
  case Sync
  case Async
}


final case class ConsumerSettings(
                                   topic: String = "test-topic",
                                   bootstrapServers: String = "localhost:9092",
                                   groupId: String = "scala-consumer",
                                   ackMode: AckMode = AckMode.Sync
                                 )

object MyBestConsumer {
  def settingsFromArgs(args: Seq[String]): ConsumerSettings =
    ConsumerSettings(
      topic = args.headOption.getOrElse("test-topic"),
      bootstrapServers = args.lift(1).getOrElse("localhost:9092"),
      groupId = args.lift(2).getOrElse("scala-consumer"),
      ackMode = args.lift(3).map(_.toLowerCase) match {
        case Some("async") => AckMode.Async
        case Some("sync")  => AckMode.Sync
        case _             => AckMode.Sync
      }
    )

  def fromSettings(settings: ConsumerSettings): MyBestConsumer =
    MyBestConsumer(settings.bootstrapServers, settings.groupId)
}

class MyBestConsumer(
                      bootstrapServers: String = "localhost:9092",
                      groupId: String = "scala-consumer"
                    ) {

  def build(): KafkaConsumer[String, String] = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")

    println(s"Bootstrap servers: $bootstrapServers")
    println(s"Consumer group: $groupId")

    new KafkaConsumer[String, String](props)
  }
}