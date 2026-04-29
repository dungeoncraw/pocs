package com.tetokeguii.logger

object LoggerFactory:
  def default(): Logger = fromResource("logger.conf")

  private def fromResource(resourceName: String): Logger =
    val config = LoggerConfigLoader.load(resourceName)
    fromConfig(config)

  private def fromConfig(config: LoggerConfig): Logger =
    val sinks = config.sinks.filter(_.enabled).map {
      case s if s.`type` == "console" => ConsoleSink()
      case s if s.`type` == "file" && s.path.isDefined => FileSink(s.path.get)
      case s => throw new IllegalArgumentException(s"Unsupported or misconfigured sink: ${s.`type`}")
    }
    val pipeline = DefaultLogPipeline(config, sinks)
    DefaultLogger(pipeline)
