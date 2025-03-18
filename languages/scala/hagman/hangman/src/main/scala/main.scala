package com.tetokeguii.hangman

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn
import scala.util.Random

val words = List("elbow", "writer", "circle", "polish", "bridge", "store", "fang", "scarecrow", "show", "jeans", "wilderness", "attempt", "waxing", "aftermath", "banana", "wrist", "wheel", "spring", "cherries", "nerve")
var word = ""
var guesses = ArrayBuffer[Char]()
var lives = 6
var mistakes = 0

@main
def hangman(): Unit =
  setupGame()

def setupGame(): Unit =
  val wordIndex = Random.nextInt(words.size)
  word = words(wordIndex).toUpperCase

  for (i <- word.indices)
    guesses.addOne('_')

  var gameOver = false
  while (!gameOver)
    printGameStatus()
    print("Guess a letter: ")
    val input = StdIn.readLine()

    if (input.isEmpty)
      println("Invalid input. Please try again.")
    else
      val letter = input.head.toUpper
      if (word.contains(letter))
        for (i <- word.indices)
          if (word(i) == letter)
            guesses(i) = letter
        if (!guesses.contains('_'))
          gameOver = true
      else
        print("Wrong letter. Try again.\n")
        mistakes += 1
        lives -= 1
        if (mistakes == 6)
          gameOver = true
  if (mistakes == 6)
    printGameStatus()
    println(s"You lost. The word was: $word")
  else
    println("You won!")
    println(s"The word was: $word")

def printGameStatus(): Unit =
  mistakes match
    case 0 => print0Mistakes()
    case 1 => print1Mistake()
    case 2 => print2Mistakes()
    case 3 => print3Mistakes()
    case 4 => print4Mistakes()
    case 5 => print5Mistakes()
    case 6 => print6Mistakes()

  print("Word: ")

  for(i <- guesses)
    print(s"$i ")

  println(s"\nLives: $lives")