package com.tetokeguii.rawkafka

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ConsumerRunnerSpec extends AnyFunSuite with Matchers {

  test("ConsumerRunner should terminate on wakeup") {
    val settings = ConsumerSettings(topic = "test-topic")
    val runner = new ConsumerRunner(settings)
    
    val thread = new Thread(() => runner.run())
    thread.start()
    
    // Give it a bit of time to start
    Thread.sleep(500)
    thread.isAlive shouldBe true
    
    runner.wakeup()
    thread.join(2000)
    
    thread.isAlive shouldBe false
  }
}
