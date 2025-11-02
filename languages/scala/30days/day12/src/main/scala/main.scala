
object MathUtils {
  // Pure functions are good for compile-time branching
  inline def square(x: Double): Double = x * x
  inline def isEven(n: Int): Boolean = n % 2 == 0

  inline def clamp(value: Double, min: Double, max: Double): Double =
    if value < min then min
    else if value > max then max
    else value
}
// should not use inline for recursive functions, it replaces the recursion infinite
inline def fibonacciInline(n: Int): Int =
  if n <= 1 then n
  else fibonacciInline(n - 1) + fibonacciInline(n - 2)

// should use tailrec for recursive functions
def fibonacciTailRec(n: Int): Int = {
  @annotation.tailrec
  def loop(n: Int, a: Int, b: Int): Int =
    if n == 0 then a
    else loop(n - 1, b, a + b)
  loop(n, 0, 1)
}
@main
def main(): Unit = {
  val result1 = MathUtils.square(5.0)
  val result2 = MathUtils.clamp(15.0, 0.0, 10.0)

}

