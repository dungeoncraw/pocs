package com.example.ioc

import com.example.ioc.service.GreetingService
import org.springframework.stereotype.Component

@Component
class Greeter(service: GreetingService) {
  def sayHello(name: String): Unit = {
    println(service.greet(name))
  }
}
