package com.tetokeguii.day07

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

enum Operation:
  case Add(left: Double, right: Double)
  case Subtract(left: Double, right: Double)
  case Multiply(left: Double, right: Double)
  case Divide(left: Double, right: Double)
  case Power(base: Double, exponent: Double)
  case Module(left: Double, right: Double)

enum Result:
  case Success(value: Double)
  case Error(message: String)

object Calculator:
  def evaluate(operation: Operation): Result =
    operation match
      case Operation.Add(left, right) =>
        Result.Success(left + right)
      case Operation.Subtract(left, right) =>
        Result.Success(left - right)
      case Operation.Multiply(left, right) =>
        Result.Success(left * right)
      case Operation.Divide(left, right) =>
        if right != 0 then Result.Success(left / right)
        else Result.Error("Cannot divide by zero")
      case Operation.Power(base, exponent) =>
        Result.Success(math.pow(base, exponent))
      case Operation.Module(left, right) =>
        Result.Success(left % right)

  def parseOperation(op: String, num1: Double, num2: Double): Option[Operation] =
    op.toLowerCase.trim match
      case "+" => Some(Operation.Add(num1, num2))
      case "-" => Some(Operation.Subtract(num1, num2))
      case "*" => Some(Operation.Multiply(num1, num2))
      case "/" => Some(Operation.Divide(num1, num2))
      case "^" | "**" => Some(Operation.Power(num1, num2))
      case "%" => Some(Operation.Module(num1, num2))
      case _ => None

  @tailrec
  def readDouble(prompt: String): Double =
    print(prompt)
    Try(StdIn.readDouble()) match
      case Success(value) => value
      case Failure(_) =>
        println("Invalid input! Please enter a valid number.")
        readDouble(prompt)

  def readOperation(): String =
    print("Enter operation (+, -, *, /, ^ for power, % for module): ")
    StdIn.readLine()

@main
def main(): Unit = {
  println("Simple calculator\nEnter two numbers (can be a floating point number) and the operation")
  println("Supported operations: +, -, *, /, ^ (power)")
  println("Type 'quit' to exit\n")

  var continue = true

  while continue do
    try
      val num1 = Calculator.readDouble("Enter first number: ")
      val operationStr = Calculator.readOperation()

      if operationStr.toLowerCase == "quit" then
        continue = false
      else
        val num2 = Calculator.readDouble("Enter second number: ")

        Calculator.parseOperation(operationStr, num1, num2) match
          case Some(operation) =>
            Calculator.evaluate(operation) match
              case Result.Success(value) =>
                println(s"Result: $value")
              case Result.Error(message) =>
                println(s"Error: $message")
          case None =>
            println("Invalid operation! Please use +, -, *, /, or ^")
        println()
    catch
      case _: Exception =>
        println("An unexpected error occurred. Please try again.")
        println()

}