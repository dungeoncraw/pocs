package com.dungeoncraw.core.variables

import scala.io.Source
import scala.util.Using

@main
def main(): Unit = {
  // simple examples
  val count = 1 + 2
  val count2: Int = 1 + 20
  val ids: List[Int] = List(1, 2, 3)
  // destructuring
  val (a, b) = (1, 2)
  // unsafe destructuring, cause if the shape doesn't match, it will throw an exception
  val List(first, second, _*) = List(1, 2, 3, 4)

  // safe destructuring with pattern matching
  val xs = List(20)
  val secondOpt: Option[Int] = xs match
    case _ :: y :: _ => Some(y)
    case _ => None

  // block-defined variables, create private variables scoped
  val total =
    val base = 100
    val tax = 20
    base + tax
  println(total)

  //  lazy variables, create variables that are evaluated only once and when needed
  lazy val config: String =  Using.resource(Source.fromResource("config.txt"))(_.mkString)
  // now computes the variable only once
  println(config)

  // Option variables
  val maybePort: Option[Int] = Some(8080)
  val port: Int = maybePort.getOrElse(80)


}

