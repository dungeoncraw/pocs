package com.example.jokes.dto

/**
 * Request DTO for creating a new joke.
 * Implements the Java interface ICreateJokeRequest for type safety and interoperability.
 * Validation is performed in the controller layer.
 *
 * @param text The joke content (required)
 * @param author Optional author name (defaults to None)
 */
final case class CreateJokeRequest(
  text: String,
  author: Option[String] = None
) extends ICreateJokeRequest {

  // Implementation of Java interface methods
  override def getText: String = text

  /**
   * Converts Scala Option to Java Optional for interface compatibility.
   * @return Java Optional containing the author, or empty Optional if not set
   */
  override def getAuthor: java.util.Optional[String] = author.map(java.util.Optional.of(_)).getOrElse(java.util.Optional.empty())
}
