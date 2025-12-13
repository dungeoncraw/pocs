package com.example.jokes.model

import java.time.Instant

final case class Joke(
  id: Long,
  text: String,
  author: Option[String],
  createdAt: Instant
)
