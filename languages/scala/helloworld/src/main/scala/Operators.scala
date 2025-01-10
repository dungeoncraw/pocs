import scala.io.StdIn

@main
def operators(): Unit = {
  println(5+2)
  println("Hi " + "John")
  println(9d * 3)
  var amount = 10
  amount += 1
  amount -= 2
  amount /= 2
  println(amount)
  amount %= 2
  println(amount)

  println("How much do you want to deposit:")
  val input = StdIn.readLine()
  val amountDeposit = input.toDouble
  val interestAmount = amountDeposit * scala.math.pow((1 + 0.055/1), 5)
  println("After five years with interest 5.5% per year")
  println(s"You will have $$$interestAmount")
}
