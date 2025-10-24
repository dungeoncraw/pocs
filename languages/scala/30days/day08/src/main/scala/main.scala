package com.tetokeguii.day08

enum Commands:
  case Up, Down, Left, Right
  case Move(x: Int, y: Int)
  case Print
  case Quit
  def exec(): Unit = this match {
    case Up => println("Up")
    case Down => println("Down")
    case Left => println("Left")
    case Right => println("Right")
    case Move(x, y) => println(s"Move($x, $y)")
    case Print => println("Print")
    case Quit => println("Quit")
  }

@main
def main(): Unit = {
  val commandList = List(Commands.Up, Commands.Down, Commands.Left, Commands.Right, Commands.Move(1, 2), Commands.Print, Commands.Quit)
  for command <- commandList yield command.exec()
}

