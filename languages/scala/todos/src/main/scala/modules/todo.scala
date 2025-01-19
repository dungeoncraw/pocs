package com.tetokeguii.modules.todo

case class Todo(code: String, description: String, date: String)

object TodoClass {
  private var todoList = List[Todo]()

  def add(item: Todo): Unit = {
    todoList = todoList.concat(List(item))
  }
  def getList(): List[Todo] = {
    todoList
  }
}