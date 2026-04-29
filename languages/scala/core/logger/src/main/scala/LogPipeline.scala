package com.tetokeguii.logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait LogPipeline:
  def process(event: LogEvent): Unit

final class DefaultLogPipeline(config: LoggerConfig, sinks: List[LogSink]) extends LogPipeline:
  override def process(event: LogEvent): Unit =
    if (event.level.ordinal >= config.level.ordinal) {
      config.delivery.mode match {
        case "sync" => sinks.foreach(_.write(event))
        case "async" =>
          Future { sinks.foreach(_.write(event)) }
      }
    }
