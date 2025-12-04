package com.tetokeguii.day23
type Username = String

type CtxFn[A] = Username ?=> A

def greet(): CtxFn[Unit] = {
  val user = summon[Username]
  println(s"Hello, $user!")
}

def farewell(): CtxFn[Unit] = {
  val user = summon[Username]
  println(s"Goodbye, $user!")
}

def showInfo(): CtxFn[Unit] = {
  val user = summon[Username]
  println(s"Current user: $user")
}

@main
def simpleContextExample(): Unit = {
  println("=== Simple Context Function Example ===\n")

  {
    given Username = "Alice"

    greet()
    showInfo()
    farewell()
  }

  println()

  {
    given Username = "Bob"

    greet()
    showInfo()
    farewell()
  }
}