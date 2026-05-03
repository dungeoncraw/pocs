package com.tetokeguii.logger

import munit.FunSuite

class LogPipelineTests extends FunSuite {

  class MockSink extends LogSink {
    var events: List[LogEvent] = Nil
    override def write(event: LogEvent): Unit = {
      events = events :+ event
    }
  }

  val syncConfig = LoggerConfig(
    level = LogLevel.Info,
    delivery = DeliveryConfig(mode = "sync"),
    sinks = Nil
  )

  test("DefaultLogPipeline should filter by level") {
    val sink = new MockSink()
    val pipeline = new DefaultLogPipeline(syncConfig, List(sink))

    pipeline.process(LogEvent(LogLevel.Debug, "debug message"))
    assertEquals(sink.events.size, 0)

    pipeline.process(LogEvent(LogLevel.Info, "info message"))
    assertEquals(sink.events.size, 1)
    assertEquals(sink.events.head.message, "info message")
  }

  test("DefaultLogPipeline should handle sync delivery") {
    val sink = new MockSink()
    val pipeline = new DefaultLogPipeline(syncConfig, List(sink))

    pipeline.process(LogEvent(LogLevel.Warn, "warn message"))
    assertEquals(sink.events.size, 1)
    assertEquals(sink.events.head.level, LogLevel.Warn)
  }

  test("DefaultLogPipeline should handle async delivery") {
    val asyncConfig = LoggerConfig(
      level = LogLevel.Info,
      delivery = DeliveryConfig(mode = "async"),
      sinks = Nil
    )
    val sink = new MockSink()
    val pipeline = new DefaultLogPipeline(asyncConfig, List(sink))

    pipeline.process(LogEvent(LogLevel.Error, "error message"))

    var attempts = 0
    while (sink.events.isEmpty && attempts < 10) {
      Thread.sleep(100)
      attempts += 1
    }

    assertEquals(sink.events.size, 1)
    assertEquals(sink.events.head.level, LogLevel.Error)
  }
}
