package com.tetokeguii.logger

trait Logger {
  def debug(message: String): Unit
  def info(message: String): Unit
  def warn(message: String): Unit
  def error(message: String): Unit
}

class DefaultLogger(pipeline: LogPipeline) extends Logger {
  override def debug(message: String): Unit = pipeline.process(LogEvent(LogLevel.Debug, message))
  override def info(message: String): Unit = pipeline.process(LogEvent(LogLevel.Info, message))
  override def warn(message: String): Unit = pipeline.process(LogEvent(LogLevel.Warn, message))
  override def error(message: String): Unit = pipeline.process(LogEvent(LogLevel.Error, message))
}