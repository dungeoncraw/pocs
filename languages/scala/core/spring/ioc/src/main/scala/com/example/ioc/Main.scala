package com.example.ioc

import com.example.ioc.config.AppConfig
import org.springframework.context.annotation.AnnotationConfigApplicationContext

@main
def main(): Unit = {
  println("Initializing Spring ApplicationContext...")

  val context = new AnnotationConfigApplicationContext(classOf[AppConfig])

  val greeter = context.getBean(classOf[Greeter])

  greeter.sayHello("Developer")

  println("Spring IoC demo finished.")
  context.close()
}
