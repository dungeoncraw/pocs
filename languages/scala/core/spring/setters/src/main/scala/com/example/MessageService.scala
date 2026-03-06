package com.example

class MessageService {
  private var _message: String = "Default Message"

  def getMessage: String = _message
  def setMessage(message: String): Unit = {
    _message = message
  }

  def printMessage(): Unit = {
    println(s"Message: $getMessage")
  }
}
