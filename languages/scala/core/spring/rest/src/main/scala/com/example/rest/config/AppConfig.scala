package com.example.rest.config

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.client.{RestOperations, RestTemplate}

@Configuration
class AppConfig {
  @Bean
  def restTemplate(): RestOperations = new RestTemplate()
}
