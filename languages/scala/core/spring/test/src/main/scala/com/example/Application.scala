package com.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.{GetMapping, RestController}
import org.springframework.beans.factory.annotation.Value

@SpringBootApplication
class Application

@RestController
class HelloController(@Value("${app.message:Hello World}") message: String) {
  
  @GetMapping(Array("/hello"))
  def hello(): String = message
}

@main
def main(args: String*): Unit = {
  SpringApplication.run(classOf[Application], args*)
}
