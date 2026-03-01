package com.example.ioc.service.impl

import com.example.ioc.service.GreetingService
import org.springframework.stereotype.Component

@Component
class WelcomeGreetingService extends GreetingService {
  override def greet(name: String): String = s"Welcome to Spring IoC with Scala, $name!"
}
