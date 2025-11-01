package com.tetokeguii.day11

trait JsonEncoder[A]:
  def encode(value: A): String

given JsonEncoder[String] with
  def encode(value: String) = s"\"$value\""

given JsonEncoder[Int] with
  def encode(value: Int) = value.toString

given JsonEncoder[Boolean] with
  def encode(value: Boolean) = value.toString

given [A](using enc: JsonEncoder[A]): JsonEncoder[List[A]] with
  def encode(values: List[A]) =
    values.map(enc.encode).mkString("[", ",", "]")

case class Person(name: String, age: Int, active: Boolean)

given JsonEncoder[Person] with
  def encode(p: Person) =
    s"""{"name":${summon[JsonEncoder[String]].encode(p.name)},"age":${p.age},"active":${p.active}}"""

case class Unknown(data: String)

def toJson[A](value: A)(using enc: JsonEncoder[A]): String = enc.encode(value)

@main
def main(): Unit = {
  // Usage
  val person = Person("Alice", 30, true)
  println(toJson(person))
  println(toJson(List(1, 2, 3)))
  println(toJson(List("a", "b")))
  //  this generates a compilation error as JsonEncoder don't know how to encode Unknown
  //  toJson(Unknown("x"))
}

