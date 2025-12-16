package com.example.jokes.model

import java.time.Instant

/**
 * Represents a joke in the system.
 * Implements the Java interface IJoke for interoperability with Java code.
 *
 * @param id Unique identifier for the joke
 * @param text The joke content
 * @param author Optional author name
 * @param createdAt Timestamp when the joke was created
 */
final case class Joke(
  id: Long,
  text: String,
  author: Option[String],
  createdAt: Instant
) extends IJoke {

  // Implementation of Java interface methods
  override def getId: Long = id

  override def getText: String = text

  /**
   * Converts Scala Option to Java Optional for interface compatibility.
   * @return Java Optional containing the author, or empty Optional if not set
   */
  override def getAuthor: java.util.Optional[String] = author.map(java.util.Optional.of(_)).getOrElse(java.util.Optional.empty())

  override def getCreatedAt: Instant = createdAt
}
