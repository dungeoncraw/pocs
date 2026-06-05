package models

import java.time.LocalDateTime
import scala.annotation.tailrec

final case class TimeSlot(
                     start: LocalDateTime,
                     end: LocalDateTime
                   )

object TimeSlot:
  def generateSlots(start: LocalDateTime, end: LocalDateTime): List[TimeSlot] =
    @tailrec
    def loop(current: LocalDateTime, acc: List[TimeSlot]): List[TimeSlot] =
      val next = current.plusMinutes(30)
      if next.isBefore(end) || next.isEqual(end) then
        loop(next, acc :+ TimeSlot(current, next))
      else
        acc

    loop(start, Nil)