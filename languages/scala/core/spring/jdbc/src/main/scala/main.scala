package user

import config.CacheConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.{Bean, Import}
import org.springframework.web.client.RestTemplate
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
@Import(Array(classOf[CacheConfig]))
class Application:
  @Bean
  def restTemplate(): RestTemplate = RestTemplate()

@main
def runApp(): Unit =
  val dotenv = Dotenv.configure().ignoreIfMissing().load()
  dotenv.entries().forEach(entry => System.setProperty(entry.getKey, entry.getValue))
  SpringApplication.run(classOf[Application])