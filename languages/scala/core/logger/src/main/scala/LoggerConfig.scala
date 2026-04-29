package com.tetokeguii.logger

case class LoggerConfig(
  level: LogLevel,
  delivery: DeliveryConfig,
  sinks: List[SinkConfig]
)

case class DeliveryConfig(
  mode: String,
  queueSize: Int = 10000,
  batchSize: Int = 100,
  flushIntervalMillis: Int = 1000
)

case class SinkConfig(
  `type`: String,
  enabled: Boolean,
  path: Option[String] = None,
  endpoint: Option[String] = None,
  index: Option[String] = None,
  format: String = "plain"
)
