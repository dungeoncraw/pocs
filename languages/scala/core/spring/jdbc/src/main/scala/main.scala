package user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class Application:
  @Bean
  def restTemplate(): RestTemplate = RestTemplate()

@main
def runApp(): Unit =
  SpringApplication.run(classOf[Application])