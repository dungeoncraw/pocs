package com.dungeoncraw.cli

import scala.util.Random

class Joke {
  private val jokes: Vector[Vector[String]] = Vector(
    Vector(
      "Why did the chicken cross the road?",
      "To get to the other side."
    ),
    Vector(
      "Why don’t skeletons fight each other?",
      "They don’t have the guts."
    ),
    Vector(
      "I’m on a seafood diet",
      "I see food and I eat it."
    ),
    Vector(
      "I would tell you a construction joke…",
      "But I’m still working on it."
    )
  )
  def tell(): Unit =
    val joke = Random.shuffle(jokes).headOption.getOrElse(Vector("So sad, no jokes today!"))
    joke.foreach(println)
}
