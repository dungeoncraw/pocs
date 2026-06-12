package models

case class TimeSlot(day: Day, hour: Int):
  def index: Int =
    Day.index(day) * 24 + hour