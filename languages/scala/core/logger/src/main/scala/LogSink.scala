package com.tetokeguii.logger

import java.nio.file.{Files, Paths, StandardOpenOption}
import java.nio.charset.StandardCharsets

trait LogSink:
  def write(event: LogEvent): Unit

final class ConsoleSink extends LogSink:
  override def write(event: LogEvent): Unit =
    println(s"[${event.level}] ${event.timestamp} - ${event.message}")

final class FileSink(path: String) extends LogSink:

  override def write(event: LogEvent): Unit =
    val line = s"[${event.level}] ${event.timestamp} - ${event.message}\n"
    val filePath = Paths.get(path)
    if (!Files.exists(filePath.getParent)) Files.createDirectories(filePath.getParent)
    Files.write(filePath, line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
