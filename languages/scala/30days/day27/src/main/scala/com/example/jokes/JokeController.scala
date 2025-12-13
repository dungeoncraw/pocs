package com.example.jokes

import com.example.jokes.dto.CreateJokeRequest
import com.example.jokes.model.Joke
import com.example.jokes.service.JokeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.net.URI

@RestController
@RequestMapping(Array("/jokes"))
class JokeController(private val jokeService: JokeService):

  @GetMapping
  def getJokes(): java.util.List[Joke] =
    // Return Java list for smooth Jackson/Boot interop, though Scala list also works with jackson-module-scala
    import scala.jdk.CollectionConverters.*
    jokeService.list().asJava

  @PostMapping
  def createJoke(@RequestBody req: CreateJokeRequest): ResponseEntity[?] =
    val text = Option(req.text).getOrElse("").trim
    val author = req.author.map(_.trim).filter(_.nonEmpty)

    val errors = collection.mutable.ListBuffer.empty[String]
    if text.isEmpty then errors += "text must not be blank"
    if text.length > 500 then errors += "text must be between 1 and 500 characters"
    author.foreach(a => if a.length > 100 then errors += "author must be at most 100 characters")

    if errors.nonEmpty then
      ResponseEntity.badRequest().body(java.util.Map.of("errors", errors.mkString(", ")))
    else
      val created = jokeService.create(CreateJokeRequest(text = text, author = author))
      val location = URI.create(s"/jokes/${created.id}")
      ResponseEntity.created(location).body(created)
