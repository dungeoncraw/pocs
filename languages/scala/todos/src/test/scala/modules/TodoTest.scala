package com.tetokeguii.modules.todo

import org.scalatest.funsuite.AnyFunSuite

class TodoTest extends AnyFunSuite {
  test("add to list") {
    val todo = new TodoClass()
    todo.add(Todo("a", "b", "c"))
    todo.add(Todo("d", "e", "f"))
    assert(todo.getList().size == 2)
  }
  test("remove one from list") {
    val todoClass = new TodoClass()
    val todo = Todo("a", "b", "c")
    todoClass.add(todo)
    todoClass.add(Todo("d", "e", "f"))
    todoClass.remove(todo)
    assert(todoClass.getList().size == 1)
  }
  test("update one from list") {
    val todoClass = new TodoClass()
    var todo = Todo("a", "b", "c")
    todoClass.add(todo)
    todoClass.add(Todo("d", "e", "f"))
    todo = Todo("a", "g", "h")
    todoClass.update(todo)
    assert(todoClass.getList().size == 2)
    assert(todoClass.getListItem("a").head == todo)
  }
}

