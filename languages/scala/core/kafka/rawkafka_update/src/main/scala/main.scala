import com.tetokeguii.rawkafka.{ConsumerApp, MyBestConsumer}

@main
def main(args: String*): Unit = {
  val settings = MyBestConsumer.settingsFromArgs(args)

  val app = ConsumerApp(
    List(
      settings.copy(topic = "test-topic"),
      settings.copy(topic = "payments"),
      settings.copy(topic = "shipments")
    )
  )
  // this is odd; creating a webhook to shut down consumer when the JVM exits,
  // this throws a WakeupException in the consumer thread, which we catch to exit gracefully
  // because kafka consumer is not thread safe, we need to ensure only one thread is accessing it at a time
  sys.addShutdownHook {
    app.stop()
  }

  app.start()
  app.awaitTermination()
}