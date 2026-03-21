package com.example.csrf

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PostMapping, ResponseBody}
import org.springframework.ui.Model
import org.springframework.security.config.Customizer

@SpringBootApplication
class CsrfApplication

object CsrfApplication {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[CsrfApplication], args*)
  }
}

@Controller
class MyCustomController {
  // need to explicitly specify the path, otherwise the default path is used, and the controller is not registered
  @GetMapping(path = Array("/"))
  def index(model: Model): String = {
    "index"
  }

  @PostMapping(path = Array("/process"))
  @ResponseBody
  def process(): String = {
    "Success: CSRF token was valid! Request processed successfully."
  }

  // need to explicitly specify the path, otherwise the default path is used, and the controller is not registered
  @GetMapping(path = Array("/health"))
  @ResponseBody
  def health(): String = {
    "OK: Controller is registered."
  }
}

@Configuration
@EnableWebSecurity
class SecurityConfig {

  @Bean
  def securityFilterChain(http: HttpSecurity): SecurityFilterChain = {
    http
      .authorizeHttpRequests(auth => auth
        .anyRequest().permitAll()
      )
      // CSRF is enabled by default in Spring Security.
      .csrf(Customizer.withDefaults())
      .build()
  }
}
