package com.example.jokes.model;

import java.time.Instant;
import java.util.Optional;

/**
 * Interface representing a joke entity.
 * Implemented by the Joke case class (Scala) to provide interoperability
 * with Java code and enable interface-based programming.
 *
 * This interface defines the contract for accessing joke data.
 */
public interface IJoke {

  /**
   * Gets the unique identifier of the joke.
   *
   * @return The joke ID
   */
  long getId();

  /**
   * Gets the text content of the joke.
   *
   * @return The joke text
   */
  String getText();

  /**
   * Gets the optional author of the joke.
   *
   * @return Optional containing the author name, or empty if not set
   */
  Optional<String> getAuthor();

  /**
   * Gets the timestamp when the joke was created.
   *
   * @return The creation timestamp
   */
  Instant getCreatedAt();
}

