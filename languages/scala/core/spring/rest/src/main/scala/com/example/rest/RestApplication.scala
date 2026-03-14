package com.example.rest

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class RestApplication

@main
def run(args: String*): Unit = {
  SpringApplication.run(classOf[RestApplication], args*)
}
