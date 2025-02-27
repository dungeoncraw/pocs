import scala.io.StdIn

@main
def conditionals(): Unit = {
  val night = true
  if (night)
    println("Good night")
  else
    println("Good morning")

  println("Please enter the amount")
  val amount = StdIn.readLine().toInt
  if (amount > 100)
    println("Amount is too high")
    println("Adjust it")
  else if (amount < 100)
    println("Amount is too low")

  // can be in single line too
  if (amount > 100) println("Amount is too high again") else println("Amount is too low again")

  val condition = if (amount > 100) "Amount is too high" else "Amount is too low"
  println(condition)

}