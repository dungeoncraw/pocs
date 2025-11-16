package com.tetokeguii.day15

inline def power(x: Double, inline n: Int): Double =
  if (n == 0) 1.0
  else if (n % 2 == 1) x * power(x, n - 1)
  else power(x * x, n / 2)

inline def half(x: Any): Any =
  inline x match
    case x: Int => x / 2
    case x: String => x.substring(0, x.length / 2)
@main
def main(): Unit = {
  println(power(2, 2))
  println(half(2))
  println(half("hello world"))
}

