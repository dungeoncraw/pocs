package com.tetokeguii.logger

enum LogLevel:
  case Debug, Info, Warn, Error

case class LogEvent(level: LogLevel, message: String, timestamp: Long = System.currentTimeMillis())
