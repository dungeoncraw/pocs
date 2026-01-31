package com.dungeoncraw.cli

import scala.io.StdIn.readLine

enum Command:
  case Greet(name: String)
  case Help
  case Quit
  case Joke
  case Unknown(command: String)

object Command:
  def parse(input: String): Command =
    input.trim.split("\\s+").toList match
      case "greet" :: name :: Nil => Greet(name)
      case "greet" :: Nil         => Greet("Stranger")
      case "joke" :: Nil          => Joke
      case "help" :: Nil          => Help
      case "quit" :: Nil          => Quit
      case _                      => Unknown(input)

@main
def main(): Unit = {
  println("Welcome to the Scala Pattern Matching CLI!")
  println("Type 'help' for available commands.")

  var continue = true
  while (continue) {
    print("> ")
    val input = readLine()
    if (input == null) {
      continue = false
    } else {
      Command.parse(input) match
        case Command.Greet(name) =>
          println(s"Hello, $name! Nice to meet you.")
        case Command.Joke => new Joke().tell()
        case Command.Help =>
          println("Available commands:")
          println("  greet [name] - Greet someone (defaults to Stranger)")
          println("  joke         - tell some joke")
          println("  help         - Show this help message")
          println("  quit         - Exit the application")
        case Command.Quit =>
          println("Goodbye!")
          continue = false
        case Command.Unknown(cmd) =>
          println(s"Unknown command: $cmd. Type 'help' for assistance.")
    }
  }
}

