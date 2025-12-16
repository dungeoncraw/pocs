package com.example.jokes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication

/**
 * Main Spring Boot application class for the Jokes API.
 * Enables auto-configuration and component scanning.
 */
@SpringBootApplication
class JokesApplication

/**
 * Companion object containing the main entry point for the application.
 */
object JokesApplication:
  /**
   * Starts the Spring Boot application.
   *
   * @param args command line arguments
   */
  def main(args: Array[String]): Unit =
    SpringApplication.run(classOf[JokesApplication], args*)
