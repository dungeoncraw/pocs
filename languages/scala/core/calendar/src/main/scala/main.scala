import models.{Person, TimeSlot}
import service.CalendarService

import java.time.{Duration, LocalDate, LocalDateTime, LocalTime}

@main
def main(): Unit =

  val calendar =
    CalendarService()

  val day =
    LocalDate.of(2026, 6, 5)

  val workStart = LocalDateTime.of(day, LocalTime.of(9, 0))
  val workEnd = LocalDateTime.of(day, LocalTime.of(18, 0))
  val slots = TimeSlot.generateSlots(workStart, workEnd)

  val ian =
    Person("1", "Ian", slots)

  val maclean =
    Person("2", "MacLean", slots)

  val senior =
    Person("3", "Senior", slots)

  val meeting1 =
    calendar.bookMeeting(
      title = "Planning",
      participants = List(ian, maclean),
      start = LocalDateTime.of(day, LocalTime.of(10, 0)),
      end = LocalDateTime.of(day, LocalTime.of(11, 0))
    )
  println(s"Booked Meeting 1: $meeting1")

  val lateMeeting =
    calendar.bookMeeting(
      title = "Late Meeting",
      participants = List(ian),
      start = LocalDateTime.of(day, LocalTime.of(18, 0)),
      end = LocalDateTime.of(day, LocalTime.of(19, 0))
    )
  println(s"Late Meeting (Should fail): $lateMeeting")

  val meeting2 =
    calendar.bookMeeting(
      title = "Design Review",
      participants = List(ian, senior),
      start = LocalDateTime.of(day, LocalTime.of(11, 30)),
      end = LocalDateTime.of(day, LocalTime.of(12, 30))
    )
  println(s"Booked Meeting 2: $meeting2")

  val conflict =
    calendar.bookMeeting(
      title = "Conflict Meeting",
      participants = List(ian),
      start = LocalDateTime.of(day, LocalTime.of(10, 30)),
      end = LocalDateTime.of(day, LocalTime.of(11, 30))
    )
  println(s"Conflict: $conflict")

  val bestTime =
    calendar.suggestBestTime(
      personA = ian,
      personB = maclean,
      day = day,
      duration = Duration.ofMinutes(60)
    )

  println(s"Best time for Ian and MacLean: $bestTime")

  println(s"Remove M-1: ${calendar.removeMeeting("M-1")}")

  println(s"Best time after removing M-1: ${calendar.suggestBestTime(personA = ian, personB = maclean, day = day, duration = Duration.ofMinutes(60))}")