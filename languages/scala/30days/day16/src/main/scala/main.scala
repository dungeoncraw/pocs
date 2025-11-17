package com.tetokeguii.day16

@main
def main(): Unit = {
  val x = 10
  val y = 32

  val z = dbg(x + y)
  dbg {
    val a = 2
    val b = 3
    a * b + z
  }
}