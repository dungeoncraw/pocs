package com.tetokeguii.rawkafka

final class ConsumerApp(settings: List[ConsumerSettings]) {

  private[rawkafka] val runners: List[ConsumerRunner] =
    settings.map(ConsumerRunner(_))

  private[rawkafka] val threads: List[Thread] =
    runners.zipWithIndex.map { case (runner, index) =>
      Thread(() => runner.run(), s"kafka-consumer-$index")
    }

  def start(): Unit =
    threads.foreach(_.start())

  def stop(): Unit =
    runners.foreach(_.wakeup())

  def awaitTermination(): Unit =
    threads.foreach(_.join())
}