package com.example.jokes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication

@SpringBootApplication
class JokesApplication

object JokesApplication:
  def main(args: Array[String]): Unit =
    SpringApplication.run(classOf[JokesApplication], args*)
