package config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.{Bean, Configuration}

import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig:

  @Bean
  def cacheManager(): CacheManager =
    val manager = new CaffeineCacheManager("users", "userById", "externalData")
    manager.setCaffeine(
      Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
    )
    manager