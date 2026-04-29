package com.tetokeguii.logger

@main
def main(): Unit = {
  val logger = LoggerFactory.default()

  logger.info("Application started")
  logger.debug("This should not be visible if level is info")
  logger.warn("Cache is empty")
  logger.error("Failed to process request")

  Thread.sleep(500)
}

