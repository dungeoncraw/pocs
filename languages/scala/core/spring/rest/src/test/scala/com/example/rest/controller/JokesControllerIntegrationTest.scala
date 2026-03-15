package com.example.rest.controller

import com.example.rest.service.JokeResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.when
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.bean.`override`.mockito.MockitoBean
import org.springframework.web.client.RestOperations
import scala.compiletime.uninitialized

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class JokesControllerIntegrationTest {

  @Autowired
  private var restTemplate: TestRestTemplate = uninitialized

  @MockitoBean
  private var mockRestOperations: RestOperations = uninitialized

  @Test
  def testFetchJokeIntegration(): Unit = {
    val mockResponse = new JokeResponse()
    mockResponse.setup = "Why did the chicken cross the road?"
    mockResponse.delivery = "To get to the other side."

    when(mockRestOperations.getForObject(
      "https://v2.jokeapi.dev/joke/any?type=twopart",
      classOf[JokeResponse]
    )).thenReturn(mockResponse)

    val responseEntity = restTemplate.getForEntity("/jokes/fetch", classOf[String])

    assertEquals(200, responseEntity.getStatusCode.value())
    assertEquals("Why did the chicken cross the road?\nTo get to the other side.", responseEntity.getBody)
  }
}
