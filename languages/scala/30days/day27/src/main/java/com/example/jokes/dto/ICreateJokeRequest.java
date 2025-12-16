package com.example.jokes.dto;

import java.util.Optional;

/**
 * Interface representing a request to create a new joke.
 * Implemented by the CreateJokeRequest case class (Scala) to provide
 * interoperability with Java code and enable interface-based programming.
 *
 * This interface defines the contract for accessing request data
 * during joke creation.
 */
public interface ICreateJokeRequest {

  /**
   * Gets the text content for the new joke.
   *
   * @return The joke text
   */
  String getText();

  /**
   * Gets the optional author for the new joke.
   *
   * @return Optional containing the author name, or empty if not provided
   */
  Optional<String> getAuthor();
}

