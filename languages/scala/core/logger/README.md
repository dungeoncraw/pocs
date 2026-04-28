# Logger

A configurable logging system for Scala applications.

The goal of this project is to provide a single, simple Logger API while allowing logging behavior to be configured from a classpath resource.

Application code should not need to know whether logs are written to the file system, sent to ELK, or delivered synchronously/asynchronously.

## Goal

Build a logging system where:

- application code uses one common Logger interface
- logging destinations are configured externally
- sync/async delivery is configured externally
- file system, ELK, and future log systems can be supported
- infrastructure choices do not leak into business code

## Example application usage

    val logger = LoggerFactory.default()

    logger.info("Application started")
    logger.error("Payment failed")

The same API should work regardless of whether logs are written to:

- local files
- ELK
- console
- Kafka
- another custom log backend

## Design Principle

The client should not configure logging mechanics directly.

Avoid exposing APIs like:

    LoggerBuilder()
      .toFile("logs/app.log")
      .async()
      .build()

Instead, logging behavior should come from a resource configuration file.

Preferred usage:

    val logger = LoggerFactory.default()

Where LoggerFactory.default() loads logging configuration from the classpath.

## Project Structure

    logger
    ├── build.sbt
    ├── README.md
    ├── src
    │   ├── main
    │   │   ├── resources
    │   │   │   └── logger.conf
    │   │   └── scala
    │   │       └── ...
    │   └── test
    │       ├── resources
    │       │   └── logger.conf
    │       └── scala
    │           └── ...

## Configuration

Logging configuration should live in:

    src/main/resources/logger.conf

Test-specific configuration can live in:

    src/test/resources/logger.conf

The test config can override production behavior, for example by using synchronous or in-memory logging.

## Example logger.conf

    logger {
      level = "info"

      delivery {
        mode = "async"
        queueSize = 10000
        batchSize = 100
        flushIntervalMillis = 1000
      }

      sinks = [
        {
          type = "file"
          enabled = true
          path = "logs/app.log"
          format = "json"
        },
        {
          type = "elk"
          enabled = false
          endpoint = "http://localhost:9200"
          index = "application-logs"
          format = "json"
        }
      ]
    }

## Core Concepts

### Logger

The application-facing API.

It should expose simple logging methods such as:

    trait Logger {
      def debug(message: String): Unit
      def info(message: String): Unit
      def warn(message: String): Unit
      def error(message: String): Unit
    }

Application code should depend only on this interface.

### LoggerFactory

Responsible for creating a configured logger.

Example:

    val logger = LoggerFactory.default()

The default factory should load logger.conf from the classpath.

Possible factory methods:

    LoggerFactory.default()
    LoggerFactory.fromResource("logger.conf")
    LoggerFactory.fromConfig(config)

### LoggerConfigLoader

Responsible for reading logging configuration from resources.

Responsibilities:

- load logger.conf
- parse it into LoggerConfig
- validate required fields
- return a typed configuration model

### LoggerConfig

A typed representation of the resource config.

Conceptually:

    final case class LoggerConfig(
      level: LogLevel,
      delivery: DeliveryConfig,
      sinks: List[SinkConfig]
    )

### LogPipeline

Internal component responsible for processing log events.

The pipeline may handle:

- level filtering
- formatting
- enrichment
- dispatching
- error handling
- batching
- retries

The application should not interact with this directly.

### LogSink

A destination for log events.

Example:

    trait LogSink {
      def write(event: LogEvent): Unit
    }

Possible implementations:

    final class FileSink extends LogSink
    final class ElkSink extends LogSink
    final class ConsoleSink extends LogSink

### Delivery Strategy

Controls how logs are delivered.

Supported modes:

- sync
- async

This should be configured in logger.conf, not in application code.

Example:

    delivery {
      mode = "async"
    }

## High-Level Architecture

    Application Code
          |
          v
    Logger
          |
          v
    LogPipeline
          |
          v
    Delivery Strategy
          |
          v
    LogSink(s)
          |
          v
    File System / ELK / Console / Other

## Runtime Flow

When application code calls:

    logger.info("Order created")

The system should:

1. Create a LogEvent
2. Check the configured log level
3. Pass the event through the log pipeline
4. Dispatch it using the configured delivery strategy
5. Write it to the configured sink or sinks

## Sync vs Async Delivery

### Sync

Synchronous logging writes the event before returning to the caller.

Pros:

- simple
- predictable
- useful for tests
- useful for local development

Cons:

- can slow down application code
- backend issues may affect request latency

### Async

Asynchronous logging queues the event and writes it in the background.

Pros:

- better application throughput
- suitable for production
- supports batching

Cons:

- more complex
- requires queue management
- logs may be lost if the process crashes before flushing

## Recommended Defaults

Suggested defaults:

    logger {
      level = "info"

      delivery {
        mode = "async"
        queueSize = 10000
        batchSize = 100
        flushIntervalMillis = 1000
      }

      sinks = [
        {
          type = "file"
          enabled = true
          path = "logs/app.log"
          format = "json"
        }
      ]
    }

For tests:

    logger {
      level = "debug"

      delivery {
        mode = "sync"
      }

      sinks = [
        {
          type = "console"
          enabled = true
          format = "plain"
        }
      ]
    }

## What This Project Should Avoid

Avoid making application code responsible for logging infrastructure.

Do not require callers to choose delivery mechanics directly:

    logger.asyncInfo("message")
    logger.syncInfo("message")

Do not require business code to know about destinations:

    fileLogger.write("message")
    elkLogger.send("message")

Do not expose routing concepts unless conditional routing is truly required.

For most cases, this is enough:

    Logger -> Pipeline -> Sink

A router is only useful if logs must be conditionally sent to different destinations based on level, category, tenant, or another rule.

## Extending the System

To add a new destination:

1. Create a new LogSink implementation
2. Add a new config type for that sink
3. Update the factory/pipeline wiring
4. Add resource configuration for the new sink

Example future sinks:

- Kafka
- database
- cloud logging provider
- HTTP endpoint
- in-memory test sink

## Example Usage

    object Application:
      def main(args: Array[String]): Unit =
        val logger = LoggerFactory.default()

        logger.info("Application started")
        logger.warn("Cache is empty")
        logger.error("Failed to process request")

The same code should work regardless of this configuration:

    delivery {
      mode = "sync"
    }

or:

    delivery {
      mode = "async"
    }

## Summary

This project provides a resource-configured logging system.

The main idea is:

    One Logger API.
    Configurable sinks.
    Configurable delivery mode.
    No infrastructure leakage into application code.

Application code logs messages.

Configuration decides where and how those messages are written.
