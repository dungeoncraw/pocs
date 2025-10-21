package org.tetokeguii.day06

import java.time.*

@main
def main(): Unit = {
  val s1 = Student("Mary", "123")
  val s2 = Student("Mary", "123", LocalDate.now())
  val s3 = Student("Mary", "123", 456)
  println(s1)
  println(s1.studentId)
  println(s2)
  println(s2.studentId)
  println(s3)
  println(s3.studentId)
}

