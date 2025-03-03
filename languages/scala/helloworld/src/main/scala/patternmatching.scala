import scala.io.StdIn
import scala.util.Random

@main
def patternMatching(): Unit = {
  println("Type one animal")
  val input = StdIn.readLine()
  input match {
    case "dog" => println("Woof")
    case "cat" => println("Meow")
    case _ => println("Unknown animal")
  }

  val onomatopoeia = input match {
    case "dog" => "Woof"
    case "cat" => "Meow"
    case _ => "Grrrer"
  }
  println(onomatopoeia)

  val feed = input match
    case "dog" | "crocodile" => "meat"
    case "cat" | "bird" => "fish"
    case _ => "Run"
  println(feed)

  val animalFeed = ("cat", "catFood")
  animalFeed match
    case ("cat", _) => println("Cat eats cat food")
    case ("dog", _) => println("Dog eats dog food")
    case _ => println("Unknown animal")

  val number = Random.nextInt(100)
  number % 2 match {
    case 0 => println(s"$number is even")
    case _ => println(s"$number is odd")
  }
  number match
    case x if 0 until 10 contains number => println(s"$number is single digit")
    case y if 10 until 100 contains number => println(s"$number is double digit")
    case _ => println(s"$number is triple digit")

  // guards
  val weekDay = "Saturday"
  val isAtm = true
  weekDay match {
    case "Saturday" if isAtm => println("Go to dojo")
    case "Sunday" => println("Go to gym")
    case "Saturday" | "Sunday" if !isAtm => println("Go to work")
    case _ => println("Go home")
  }
}