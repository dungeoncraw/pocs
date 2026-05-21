package com.tetokeguii.rawkafka

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ConsumerAppSpec extends AnyFunSuite with Matchers {

  test("ConsumerApp should initialize runners and threads") {
    val settings = List(
      ConsumerSettings(topic = "topic1"),
      ConsumerSettings(topic = "topic2")
    )
    val app = new ConsumerApp(settings)
    
    app.runners should have size 2
    app.threads should have size 2
    app.threads.map(_.getName) should contain allOf ("kafka-consumer-0", "kafka-consumer-1")

    app.start()
    app.threads.foreach(t => t.isAlive shouldBe true)
    
    app.stop()
    app.awaitTermination()
    
    app.threads.foreach(t => t.isAlive shouldBe false)
  }
}
