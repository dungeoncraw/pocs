package com.example

import org.springframework.context.annotation.{Bean, Configuration, ComponentScan}

@Configuration
@ComponentScan(basePackages = Array("com.example"))
class AppConfig {
  @Bean
  def messageService(): MessageService = {
    // this is setter injection, so all the configuration is done before the service is created
    val service = new MessageService()
    service.setMessage("Hello from Setter!")
    service
  }

  @Bean
  def constructorMessageService(): ConstructorMessageService = {
    new ConstructorMessageService("Hello from Spring Constructor Injection!")
  }
}
