package com.example.jokes

import com.example.jokes.dto.CreateJokeRequest
import com.example.jokes.model.Joke
import com.example.jokes.service.IJokeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.net.URI

/**
 * REST Controller for managing jokes.
 * Depends on IJokeService (Java interface) for business logic.
 *
 * @param jokeService Service interface for joke operations
 */
@RestController
@RequestMapping(Array("/jokes"))
class JokeController(private val jokeService: IJokeService):

  /**
   * Retrieves all jokes.
   *
   * @return List of all jokes from the service
   */
  @GetMapping
  def getJokes(): java.util.List[Joke] =
    // Returns Java list from the service which implements IJokeService
    jokeService.list()

  /**
   * Creates a new joke with validation.
   *
   * @param req Request object containing joke text and optional author
   * @return ResponseEntity with created joke and 201 status, or error response with 400 status
   */
  @PostMapping
  def createJoke(@RequestBody req: CreateJokeRequest): ResponseEntity[?] =
    // Extract and trim text
    val text = Option(req.text).getOrElse("").trim
    // Extract and filter empty author values
    val author = req.author.map(_.trim).filter(_.nonEmpty)

    // Validate input
    val errors = collection.mutable.ListBuffer.empty[String]
    if text.isEmpty then errors += "text must not be blank"
    if text.length > 500 then errors += "text must be between 1 and 500 characters"
    author.foreach(a => if a.length > 100 then errors += "author must be at most 100 characters")

    // Return error response if validation fails
    if errors.nonEmpty then
      ResponseEntity.badRequest().body(java.util.Map.of("errors", errors.mkString(", ")))
    else
      // Create the joke and return 201 Created with location header
      val created = jokeService.create(CreateJokeRequest(text = text, author = author))
      val location = URI.create(s"/jokes/${created.id}")
      ResponseEntity.created(location).body(created)
