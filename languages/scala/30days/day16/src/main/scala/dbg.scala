package com.tetokeguii.day16

import scala.quoted.*

inline def dbg[A](inline expr: A): A =
  ${ dbgImpl('expr) }

private def dbgImpl[A: Type](expr: Expr[A])(using Quotes): Expr[A] =

  val code: String = expr.show

  val codeExpr: Expr[String] = Expr(code)

  '{
    val value = $expr
    println("[dbg] " + $codeExpr + " = " + value)
    value
  }