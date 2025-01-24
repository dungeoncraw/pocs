package com.tetokeguii.modules.todo

case class Todo(code: String, description: String, date: String)

class TodoClass {
  private var todoList = List[Todo]()

  def add(item: Todo): Unit = {
    todoList = todoList.appended(item)
  }
  def getList(): List[Todo] = {
    todoList
  }
  def getListItem(id: String): List[Todo] = {
    todoList.filter(_.code == id)
  }
  def remove(item: Todo): Unit = {
    todoList = todoList.filter(_.code != item.code)
  }
  def update(item: Todo): Unit = {
    todoList = todoList.filter(_.code != item.code).appended(item)
  }
}