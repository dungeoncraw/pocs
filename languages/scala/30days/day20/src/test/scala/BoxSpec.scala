package com.tetokeguii.day20

import org.scalatest.funsuite.AnyFunSuite

// ScalaTest suite covering Box, identity, and swap
class BoxSpec extends AnyFunSuite {
  test("Box.get returns the stored value and isEmpty is always false for this impl") {
    val intBox = Box(42)
    val strBox = Box("hello")

    assert(intBox.get == 42)
    assert(strBox.get == "hello")
    assert(!intBox.isEmpty)
    assert(!strBox.isEmpty)
  }

  test("identity returns the same value with the same type") {
    val n = identity(123)
    val s = identity("scala")

    assert(n == 123)
    assert(s == "scala")
  }

  test("swap swaps the elements of a tuple preserving types") {
    val p1 = (1, "two")
    val swapped1 = swap(p1)
    assert(swapped1 == ("two", 1))

    val p2 = (true, 9.5)
    val swapped2 = swap(p2)
    assert(swapped2 == (9.5, true))
  }
}
