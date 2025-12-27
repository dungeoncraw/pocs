package com.tetokeguii.aurora.core

import scala.quoted.*

object Debug:
  inline def dbg(inline expr: Any): Unit = ${ dbgImpl('expr) }

  private def dbgImpl(expr: Expr[Any])(using Quotes): Expr[Unit] =
    val sourceExpr = Expr(expr.show)
    '{
      if (System.getProperty("aurora.debug") == "true") {
        val result = $expr
        println(s"DEBUG: ${$sourceExpr} = $result")
      }
    }
