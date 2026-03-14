package com.example.rest.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.springframework.web.client.RestOperations

class JokeServiceTest {

  @Test
  def testFetchJokeSuccess(): Unit = {
    val restOperations = mock(classOf[RestOperations])
    val jokeService = new JokeServiceImpl(restOperations)
    
    val mockResponse = new JokeResponse()
    mockResponse.setup = "Why did the chicken cross the road?"
    mockResponse.delivery = "To get to the other side."
    
    when(restOperations.getForObject(anyString(), any[Class[JokeResponse]]()))
      .thenReturn(mockResponse)
      
    val result = jokeService.fetch()
    
    assertEquals("Why did the chicken cross the road?\nTo get to the other side.", result)
    verify(restOperations).getForObject(anyString(), any[Class[JokeResponse]]())
  }

  @Test
  def testFetchJokeNullResponse(): Unit = {
    val restOperations = mock(classOf[RestOperations])
    val jokeService = new JokeServiceImpl(restOperations)
    
    when(restOperations.getForObject(anyString(), any[Class[JokeResponse]]()))
      .thenReturn(null)
      
    val result = jokeService.fetch()
    
    assertEquals("No joke received.", result)
  }
}
