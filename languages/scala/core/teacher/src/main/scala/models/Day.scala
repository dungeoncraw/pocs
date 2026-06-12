package models

enum Day:
  case Monday, Tuesday, Wednesday, Thursday, Friday

object Day:
  val all: Vector[Day] =
    Vector(Monday, Tuesday, Wednesday, Thursday, Friday)

  def index(day: Day): Int =
    all.indexOf(day)

  def minusWorkDays(day: Day, days: Int): Day =
    val safeIndex =
      math.max(0, index(day) - days)

    all(safeIndex)

