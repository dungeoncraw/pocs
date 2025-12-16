package com.example.jokes.service

import com.example.jokes.dto.CreateJokeRequest
import com.example.jokes.model.Joke

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import scala.jdk.CollectionConverters.*
import org.springframework.stereotype.Service

/**
 * Service class for managing jokes.
 * Implements the Java interface IJokeService for contract-based design and interoperability.
 * Stores jokes in an in-memory concurrent hash map.
 */
@Service
class JokeService extends IJokeService {
  // Thread-safe atomic counter for generating unique joke IDs
  private val idSeq = AtomicLong(0L)

  // Thread-safe map for storing jokes (ID -> Joke)
  private val store = new ConcurrentHashMap[Long, Joke]()

  /**
   * Retrieves all jokes sorted by ID.
   * Converts from Scala List to Java List for interface compliance.
   *
   * @return Java List of all jokes sorted by ID
   */
  override def list(): java.util.List[Joke] =
    store.values().asScala.toList.sortBy(_.id).asJava

  /**
   * Creates a new joke with the provided request data.
   * Generates a unique ID, timestamps the creation, and stores the joke.
   *
   * @param req Request object containing joke text and optional author
   * @return The created Joke object with assigned ID and timestamp
   */
  override def create(req: CreateJokeRequest): Joke =
    // Generate unique ID for the new joke
    val id = idSeq.incrementAndGet()
    // Create joke instance with trimmed text and author
    val joke = Joke(
      id = id,
      text = req.text.trim,
      author = req.author.map(_.trim).filter(_.nonEmpty),
      createdAt = Instant.now()
    )
    // Store the joke in the map
    store.put(id, joke)
    // Return the created joke
    joke
}
