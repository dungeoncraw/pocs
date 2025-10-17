package com.tetokeguii.day01

@main
def main(): Unit = {
  val regularNumbers = List.range(1,16)
  val even = regularNumbers.filter(_ % 2 == 0)
  even.foreach(println)
  even.map(_ * 10).foreach {
    case x if x <50 => println(s"Item less than 50: $x")
    case x if x <= 100 => println(s"Item greater than 50 but less than 100: $x")
    case x => println(s"Item greater than 100: $x")
  }
}

