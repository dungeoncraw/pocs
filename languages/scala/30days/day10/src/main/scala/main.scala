
class Box[T](value: T) {
  def get: T = value
  def isEmpty: Boolean = false
}
def identity[T](x: T): T = x
def swap[A, B](pair: (A, B)): (B, A) = (pair._2, pair._1)

@main
def main(): Unit = {
  val stringBox = Box("Hello")
  val intBox = Box(42)
  val boolBox = Box(true)
  println(stringBox)
  println(intBox)
  println(boolBox)
  val result1 = identity(42)
  val result2 = identity("hello")
  val swapped = swap((1, "two"))
  println(swapped)
  println(result1)
  println(result2)
}

