package com.dungeoncraw.condominium

import scala.collection.mutable.ListBuffer

final case class ApartmentUnit(number: String, owner: String)
final case class Bill(unitNumber: String, description: String, amount: BigDecimal)

@main def main(): Unit = {
  val units: Vector[ApartmentUnit] = Vector(
    ApartmentUnit("A-101", "Mina"),
    ApartmentUnit("A-102", "Noah"),
    ApartmentUnit("B-201", "Isha")
  )

  val unitsWithNewOwner =
    units.map(u => if (u.number == "A-102") u.copy(owner = "Noah & Sam") else u)

  println("Original units:")
  units.foreach(println)

  println("\nUpdated units (new Vector):")
  unitsWithNewOwner.foreach(println)

  val bills: ListBuffer[Bill] = ListBuffer.empty

  bills += Bill("A-101", "Water",    28.50)
  bills += Bill("A-102", "Elevator", 15.00)
  bills += Bill("B-201", "Cleaning", 22.75)

  bills -= Bill("A-102", "Elevator", 15.00)

  val idx = bills.indexWhere(b => b.unitNumber == "B-201" && b.description == "Cleaning")
  if (idx >= 0) bills.update(idx, bills(idx).copy(amount = 25.00))

  println("\nCurrent mutable bills:")
  bills.foreach(println)

  val total = bills.view.map(_.amount).sum
  println(s"\nTotal billed: $$${total}")
}