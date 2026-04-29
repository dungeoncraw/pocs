package com.tetokeguii.logger

import scala.jdk.CollectionConverters._

object LoggerConfigLoader:
  def load(resourceName: String): LoggerConfig =
    val conf = com.typesafe.config.ConfigFactory.load(resourceName).getConfig("logger")
    val level = LogLevel.valueOf(conf.getString("level").capitalize)
    
    val deliveryConf = conf.getConfig("delivery")
    val delivery = DeliveryConfig(
      mode = deliveryConf.getString("mode"),
      queueSize = if (deliveryConf.hasPath("queueSize")) deliveryConf.getInt("queueSize") else 10000,
      batchSize = if (deliveryConf.hasPath("batchSize")) deliveryConf.getInt("batchSize") else 100,
      flushIntervalMillis = if (deliveryConf.hasPath("flushIntervalMillis")) deliveryConf.getInt("flushIntervalMillis") else 1000
    )

    val sinks = conf.getConfigList("sinks").asScala.map { s =>
      SinkConfig(
        `type` = s.getString("type"),
        enabled = s.getBoolean("enabled"),
        path = if (s.hasPath("path")) Some(s.getString("path")) else None,
        endpoint = if (s.hasPath("endpoint")) Some(s.getString("endpoint")) else None,
        index = if (s.hasPath("index")) Some(s.getString("index")) else None,
        format = if (s.hasPath("format")) s.getString("format") else "plain"
      )
    }.toList

    LoggerConfig(level, delivery, sinks)
