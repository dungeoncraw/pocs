package com.example.logistics
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class LogisticsApplication

object LogisticsApplication {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[LogisticsApplication], args*)
  }
}