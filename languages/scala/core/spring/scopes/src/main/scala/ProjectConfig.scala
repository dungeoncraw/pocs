package config

import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import java.time.Clock

@Configuration
@ComponentScan(basePackages = Array("beans"))
class ProjectConfig:

  @Bean
  def clock: Clock =
    Clock.systemUTC()