package com.example

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import scala.compiletime.uninitialized

@RunWith(classOf[SpringRunner])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = Array("app.message=Test Message"))
class ApplicationTest {

  @Autowired
  var webTestClient: WebTestClient = uninitialized

  @Autowired
  var environment: Environment = uninitialized

  @Test
  def testEnvironment(): Unit = {
    val message = environment.getProperty("app.message")
    assertEquals("Test Message", message)
  }

  @Test
  def testHelloEndpoint(): Unit = {
    webTestClient.get().uri("/hello")
      .exchange()
      .expectStatus().isOk
      .expectBody(classOf[String]).isEqualTo("Test Message")
  }
}
