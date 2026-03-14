package com.example.rest.service

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.stereotype.Service
import org.springframework.web.client.RestOperations

trait JokeService {
  def fetch(): String
}

@Service
class JokeServiceImpl(val restTemplate: RestOperations) extends JokeService {

  override def fetch(): String = {
    val response = restTemplate.getForObject(
      "https://v2.jokeapi.dev/joke/any?type=twopart",
      classOf[JokeResponse]
    )

    if response == null then
      "No joke received."
    else
      s"${response.setup}\n${response.delivery}"
  }
}

class JokeResponse {
  @JsonProperty("error")
  var error: Boolean = false
  @JsonProperty("category")
  var category: String = ""
  @JsonProperty("type")
  var `type`: String = ""
  @JsonProperty("setup")
  var setup: String = ""
  @JsonProperty("delivery")
  var delivery: String = ""
}