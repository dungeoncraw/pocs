import scala.util.Random

@main
def main(): Unit = {
  val rnd = new Random()

  val people  = Vector("Ava", "Ben", "Cara", "Diego", "Eve", "Julio", "Zek", "Yan")
  val prizes  = Vector("Gift Card", "Headphones", "Mug", "Backpack")
  val letters = ('A' to 'Z').toVector
  val numbers = 1000 to 1020

  val codes: Vector[String] =
    (for {
      ch <- letters.take(6)
      n  <- numbers
    } yield s"$ch$n")

  val ticketPool: Vector[(String, String, String)] =
    (for {
      person <- people
      prize  <- prizes
      code   <- codes
      // filter ben's headphones out
      if !(person == "Ben" && prize == "Headphones")
    } yield (person, prize, code))

  println(s"Ticket pool size: ${ticketPool.size}")

  val ticketCount = 10
  val sampled = rnd.shuffle(ticketPool.toList).take(ticketCount)

  val tickets: Vector[(Int, String, String, String)] =
    (for ((t, idx) <- sampled.zipWithIndex) yield {
      val (person, prize, code) = t
      (idx + 1, person, code, prize)
    }).toVector

  println("\nTickets:")
  for ((i, person, code, prize) <- tickets) {
    println(f"$i%2d) $person%-5s code=$code prize=$prize")
  }

  val winnerCount = 3
  val winners = rnd.shuffle(tickets).distinctBy(_._2).take(3)

  println("\nWinners:")
  for ((_, person, code, prize) <- winners) {
    println(s"$person with $code wins $prize")
  }
}