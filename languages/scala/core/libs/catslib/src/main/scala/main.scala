import cats.Monoid
import cats.syntax.show.*
import cats.syntax.monoid.*
import cats.syntax.functor.*

def average(m: Metrics): Double =
  if m.count == 0 then 0.0 else m.sum / m.count

@main
def main(): Unit = {
  val u1 = User("John", 20)
  val u2 = User("John", 20)
  val u3 = User("Jane", 21)
  println(u1 == u2)
  println(u1 == u3)
  println(u1.show)
  println(u2.show)
  println(u3.show)
  val vala = 10
  val valb = 20
  println(vala |+| valb)
  println("Hello, " |+| "world!")

  val xs = List(1, 2, 3)
  val ys = List(4, 5)
  println(xs |+| ys)
  val total = Monoid[Int].empty |+| 5 |+| 10
  println(total) // 15

  val box = Box(10)
  val result = box.map(_ * 2)
  println(result)

  val stats = Stats(1, 10.0, 10.0)
  val stats2 = Stats(2, 20.0, 10.0)
  val combinedStats = stats |+| stats2
  println(combinedStats)

  // custom monoid
  val a = Metrics(1, 10.0)
  val b = Metrics(2, 20.0)
  val combinedMetrics = a |+| b
  println(combinedMetrics)
  println(average(combinedMetrics))
  println(Monoid[Metrics].empty)
}

