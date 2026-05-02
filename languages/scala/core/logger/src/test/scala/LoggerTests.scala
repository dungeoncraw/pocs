package com.tetokeguii.logger

import munit.FunSuite

class LoggerTests extends FunSuite {
  
  class MockPipeline extends LogPipeline {
    var lastEvent: Option[LogEvent] = None
    override def process(event: LogEvent): Unit = {
      lastEvent = Some(event)
    }
  }

  test("DefaultLogger should pass events to pipeline") {
    val pipeline = new MockPipeline()
    val logger = new DefaultLogger(pipeline)
    
    logger.info("test message")
    
    assert(pipeline.lastEvent.isDefined)
    assertEquals(pipeline.lastEvent.get.level, LogLevel.Info)
    assertEquals(pipeline.lastEvent.get.message, "test message")
  }

  test("DefaultLogger should handle all levels") {
    val pipeline = new MockPipeline()
    val logger = new DefaultLogger(pipeline)
    
    logger.debug("debug")
    assertEquals(pipeline.lastEvent.get.level, LogLevel.Debug)
    
    logger.info("info")
    assertEquals(pipeline.lastEvent.get.level, LogLevel.Info)
    
    logger.warn("warn")
    assertEquals(pipeline.lastEvent.get.level, LogLevel.Warn)
    
    logger.error("error")
    assertEquals(pipeline.lastEvent.get.level, LogLevel.Error)
  }
}
