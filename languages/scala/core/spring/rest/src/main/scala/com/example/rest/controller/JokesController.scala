package com.example.rest.controller

import com.example.rest.service.JokeService
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}

@RestController
@RequestMapping(Array("/jokes"))
class JokesController(val restService: JokeService) {
  @GetMapping(Array("/fetch"))
  def fetch(): String = restService.fetch()
}
