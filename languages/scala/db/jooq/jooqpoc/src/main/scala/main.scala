package com.tetokeguii.jooq

import java.sql.DriverManager //
//
import org.jooq._ //
import org.jooq.impl._ //
import org.jooq.impl.DSL._ //
import org.jooq.examples.scala.h2.Tables._ //
import

@main
def main(): Unit =
  val c = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "")
  val e = DSL.using(c, SQLDialect.H2)

