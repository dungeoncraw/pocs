package com.example.jokes.dto

// Using manual validation in the controller for simplicity in Scala 3
final case class CreateJokeRequest(
  text: String,
  author: Option[String] = None
)
