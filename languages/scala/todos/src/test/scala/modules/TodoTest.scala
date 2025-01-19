package com.tetokeguii.modules.todo

import org.scalatest.funsuite.AnyFunSuite

class TodoTest extends AnyFunSuite {
  test("add one to list") {
    val todo = TodoClass
    todo.add(Todo("a", "b", "c"))
    assert(todo.getList().size == 1)
  }
}

