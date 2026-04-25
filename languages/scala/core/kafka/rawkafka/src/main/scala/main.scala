import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import java.util.Properties
import scala.jdk.CollectionConverters._

object KafkaConsumer {

  def main(args: Array[String]): Unit = {
    val topic =
      if (args.nonEmpty) args(0)
      else "test-topic"

    val bootstrapServers =
      if (args.length >= 2) args(1)
      else "localhost:9092"

    val groupId =
      if (args.length >= 3) args(2)
      else "scala-consumer"

    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")

    val consumer = new KafkaConsumer[String, String](props)
    // this is odd; creating a webhook to shut down consumer when the JVM exits
    // this throw a WakeupException in the consumer thread, which we catch to exit gracefully
    // because kafka consumer is not thread safe, we need to ensure only one thread is accessing it at a time
    sys.addShutdownHook {
      consumer.wakeup()
    }

    try {
      consumer.subscribe(List(topic).asJava)
      println(s"Consuming topic: $topic")
      println(s"Bootstrap servers: $bootstrapServers")
      println(s"Consumer group: $groupId")

      while (true) {
        val records = consumer.poll(Duration.ofMillis(1000))

        records.asScala.foreach { record =>
          println(
            s"topic=${record.topic()}, partition=${record.partition()}, offset=${record.offset()}, key=${record.key()}, value=${record.value()}"
          )
        }
      }
    } catch {
      case _: org.apache.kafka.common.errors.WakeupException =>
        println("Consumer shutting down...")
    } finally {
      consumer.close()
    }
  }
}