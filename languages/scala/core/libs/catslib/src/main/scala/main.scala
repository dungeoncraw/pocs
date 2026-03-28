import cats.Monoid
import cats.syntax.show.*
import cats.syntax.monoid.*


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
}

