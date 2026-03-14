package com.example.rest.controller

import com.example.rest.service.JokeService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions._
import org.mockito.Mockito._

class JokesControllerTest {

  @Test
  def testFetchJoke(): Unit = {
    val jokeService = mock(classOf[JokeService])
    val jokesController = new JokesController(jokeService)
    
    val expectedJoke = "Why did the chicken cross the road?\nTo get to the other side."
    when(jokeService.fetch()).thenReturn(expectedJoke)

    val result = jokesController.fetch()
    
    assertEquals(expectedJoke, result)
    verify(jokeService).fetch()
  }
}
