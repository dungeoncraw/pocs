package org.tetokeguii.day06

import java.time.LocalDate
import java.time.Instant


class Student(
               var name: String,
               var govtId: String,
             ):
  private var _applicationDate: Option[LocalDate] = None
  private var _studentId: String = ""

  // multiple constructors
  def this(
            name: String,
            govtId: String,
            applicationDate: LocalDate,
          ) = {
    this(name, govtId)
    _applicationDate = Some(applicationDate)
  }

  def this(
            name: String,
            govtId: String,
            studentId: Int
          ) =
    this(name, govtId)
    _studentId = Student.setStudentId(studentId)

  def studentId: String = _studentId

object Student {
  private def setStudentId(studentId: Int): String = s"std-$studentId-${Instant.now().getEpochSecond.toString}"
}