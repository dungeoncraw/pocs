import scala.annotation.tailrec

def doubleOdds(xs: List[Int]): List[Int] =
  @tailrec
  def loop(remaining: List[Int], accReversed: List[Int]): List[Int] =
      remaining match
        case Nil =>
          accReversed.reverse
        case h :: t =>
          if h % 2 == 1 then
            loop(t, (h * 2) :: accReversed)
          else
            loop(t, accReversed)


  loop(xs, Nil)

@main def main(): Unit =
  val xs = (1 to 20).toList
  println(doubleOdds(xs))