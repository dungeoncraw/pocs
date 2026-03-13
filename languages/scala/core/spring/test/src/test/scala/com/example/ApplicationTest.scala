package com.example

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
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

  @Test
  def testGetItems(): Unit = {
    webTestClient.get().uri("/items")
      .exchange()
      .expectStatus().isOk
      .expectBody()
      .jsonPath("$[0].name").isEqualTo("Initial Item")
      .jsonPath("$[0].description").isEqualTo("This is an initial item")
      .jsonPath("$[1].name").isEqualTo("Second Item")
      .jsonPath("$[1].id").isEqualTo(2L)
  }

  @Test
  def testPostItem(): Unit = {
    val newItem = Item(0L, "New Item", "Description")
    webTestClient.post().uri("/items")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(newItem)
      .exchange()
      .expectStatus().isOk
      .expectBody().jsonPath("$.name").isEqualTo("New Item")
  }

  @Test
  def testPutItem(): Unit = {
    val updatedItem = Item(1L, "Updated Item", "Updated Description")
    webTestClient.put().uri("/items/1")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(updatedItem)
      .exchange()
      .expectStatus().isOk
      .expectBody().jsonPath("$.name").isEqualTo("Updated Item")
  }

  @Test
  def testDeleteItem(): Unit = {
    webTestClient.delete().uri("/items/1")
      .exchange()
      .expectStatus().isOk
      .expectBody(classOf[String]).isEqualTo("Item 1 deleted")
  }

  @Test
  def testSumPrimes(): Unit = {
    webTestClient.get().uri("/sum-prime?n=500")
      .exchange()
      .expectStatus().isOk
      .expectBody(classOf[String]).isEqualTo("Sum of primes up to 500 is 21536")
  }
}
