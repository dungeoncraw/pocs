package service

import models.{CalendarError, Meeting, Person, TimeSlot}

import java.time.{Duration, LocalDate, LocalDateTime, LocalTime}

class CalendarService:

  private var meetings: List[Meeting] = List.empty
  private var nextId: Int = 1

  def bookMeeting(
                   title: String,
                   participants: List[Person],
                   start: LocalDateTime,
                   end: LocalDateTime
                 ): Either[CalendarError, Meeting] =

    if !start.isBefore(end) then
      Left(CalendarError.InvalidTime)
    else
      val busyPerson =
        participants.find(person => !isAvailable(person, start, end) || isBusy(person, start, end))

      busyPerson match
        case Some(person) =>
          Left(CalendarError.PersonBusy(person))

        case None =>
          val meeting =
            Meeting(
              id = s"M-$nextId",
              title = title,
              participants = participants,
              start = start,
              end = end
            )

          nextId += 1
          meetings = meeting :: meetings

          Right(meeting)

  def removeMeeting(id: String): Either[CalendarError, Meeting] =
    meetings.find(_.id == id) match
      case None =>
        Left(CalendarError.MeetingNotFound)

      case Some(meeting) =>
        meetings = meetings.filterNot(_.id == id)
        Right(meeting)

  def listMeetings(person: Person): List[Meeting] =
    meetings
      .filter(_.participants.contains(person))
      .sortWith((a, b) => a.start.isBefore(b.start))

  def suggestBestTime(
                       personA: Person,
                       personB: Person,
                       day: LocalDate,
                       duration: Duration
                     ): Option[TimeSlot] =

    val workStart =
      LocalDateTime.of(day, LocalTime.of(9, 0))

    val workEnd =
      LocalDateTime.of(day, LocalTime.of(18, 0))

    var currentStart =
      workStart

    while currentStart.plus(duration).isBefore(workEnd) ||
      currentStart.plus(duration).isEqual(workEnd)
    do
      val currentEnd =
        currentStart.plus(duration)

      val personAFree =
        isAvailable(personA, currentStart, currentEnd) && !isBusy(personA, currentStart, currentEnd)

      val personBFree =
        isAvailable(personB, currentStart, currentEnd) && !isBusy(personB, currentStart, currentEnd)

      if personAFree && personBFree then
        return Some(TimeSlot(currentStart, currentEnd))

      currentStart = currentStart.plusMinutes(30)

    None

  private def isBusy(
                      person: Person,
                      start: LocalDateTime,
                      end: LocalDateTime
                    ): Boolean =

    meetings.exists { meeting =>
      val samePerson =
        meeting.participants.contains(person)

      val timeOverlaps =
        start.isBefore(meeting.end) && meeting.start.isBefore(end)

      samePerson && timeOverlaps
    }

  private def isAvailable(
                           person: Person,
                           start: LocalDateTime,
                           end: LocalDateTime
                         ): Boolean =
    person.timeSlots.exists { slot =>
      (slot.start.isBefore(start) || slot.start.isEqual(start)) &&
        (slot.end.isAfter(end) || slot.end.isEqual(end))
    }