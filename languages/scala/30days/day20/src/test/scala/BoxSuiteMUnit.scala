package com.tetokeguii.day20

// MUnit suite covering Box, identity, and swap
class BoxSuiteMUnit extends munit.FunSuite {
  test("Box.get returns stored value; isEmpty is false") {
    val b1 = Box(10)
    val b2 = Box("x")
    assertEquals(b1.get, 10)
    assertEquals(b2.get, "x")
    assert(!b1.isEmpty)
    assert(!b2.isEmpty)
  }

  test("identity returns the same value") {
    val n = identity(5)
    val s = identity("scala3")
    assertEquals(n, 5)
    assertEquals(s, "scala3")
  }

  test("swap swaps tuple elements") {
    val p = (true, 7)
    val s = swap(p)
    assertEquals(s, (7, true))
  }
}
