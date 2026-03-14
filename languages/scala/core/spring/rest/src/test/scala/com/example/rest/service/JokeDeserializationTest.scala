package com.example.rest.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions._

class JokeDeserializationTest {

  @Test
  def testDeserializationWithDefaultObjectMapper(): Unit = {
    val mapper = new ObjectMapper()
    val json = """{"error": false, "category": "Programming", "type": "twopart", "setup": "Why did the chicken cross the road?", "delivery": "To get to the other side."}"""
    val response = mapper.readValue(json, classOf[JokeResponse])
    
    assertFalse(response.error)
    assertEquals("Programming", response.category)
    assertEquals("twopart", response.`type`)
    assertEquals("Why did the chicken cross the road?", response.setup)
    assertEquals("To get to the other side.", response.delivery)
  }

  @Test
  def testDeserializationWithScalaModule(): Unit = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    val json = """{"setup": "Why did the chicken cross the road?", "delivery": "To get to the other side."}"""
    val response = mapper.readValue(json, classOf[JokeResponse])
    
    assertEquals("Why did the chicken cross the road?", response.setup)
    assertEquals("To get to the other side.", response.delivery)
  }
}
