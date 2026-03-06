package com.example

class ConstructorMessageService(private val message: String) {
  def printMessage(): Unit = {
    println(s"Constructor Message: $message")
  }
}
