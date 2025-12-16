package com.example.jokes.service;

import com.example.jokes.dto.CreateJokeRequest;
import com.example.jokes.model.Joke;
import java.util.List;

/**
 * Interface defining the contract for joke service operations.
 * Implemented by JokeService (Scala class) to provide interoperability
 * between Java and Scala code.
 *
 * This interface ensures type safety and allows dependency injection
 * via Spring's interface-based wiring.
 */
public interface IJokeService {

  /**
   * Retrieves all jokes from the service.
   *
   * @return List of all jokes, sorted by ID
   */
  List<Joke> list();

  /**
   * Creates a new joke in the service.
   *
   * @param req Request object containing joke details
   * @return The created Joke object with assigned ID and timestamp
   */
  Joke create(CreateJokeRequest req);
}

