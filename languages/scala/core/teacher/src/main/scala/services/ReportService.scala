package services

import models.{Schedule, ScheduledTask, Task, TimeSlot}

object ReportService:
  def printSchedule(schedule: Schedule): Unit =
    println("\n" + "=" * 40 + " SCHEDULE REPORT " + "=" * 40)
    
    if schedule.scheduled.isEmpty then
      println("No tasks scheduled.")
    else
      val header = "| %-20s | %-15s | %-10s | %-30s |".format("Task Title", "Type", "Duration", "Slots")
      val separator = "-" * header.length
      println(separator)
      println(header)
      println(separator)
      
      schedule.scheduled.sortBy(_.slots.headOption.map(_.index).getOrElse(0)).foreach { st =>
        val slotsStr = formatSlots(st.slots)
        println("| %-20.20s | %-15s | %-10d | %-30.30s |".format(
          st.task.title, 
          st.task.kind.toString, 
          st.task.durationHours, 
          slotsStr
        ))
      }
      println(separator)

    if schedule.unscheduled.nonEmpty then
      println("\n" + "!" * 10 + " UNSCHEDULED TASKS " + "!" * 10)
      val header = "| %-20s | %-15s | %-10s |".format("Task Title", "Type", "Priority")
      val separator = "-" * header.length
      println(separator)
      println(header)
      println(separator)
      schedule.unscheduled.foreach { t =>
        println("| %-20.20s | %-15s | %-10d |".format(
          t.title, 
          t.kind.toString, 
          t.priority
        ))
      }
      println(separator)

  private def formatSlots(slots: Vector[TimeSlot]): String =
    if slots.isEmpty then "-"
    else
      val grouped = slots.groupBy(_.day).toVector.sortBy(_._1.ordinal)
      grouped.map { (day, daySlots) =>
        val hours = daySlots.map(_.hour).sorted
        val intervals = toIntervals(hours)
        s"$day: ${intervals.mkString(",")}"
      }.mkString("; ")

  private def toIntervals(hours: Seq[Int]): Seq[String] =
    if hours.isEmpty then Seq.empty
    else
      val sorted = hours.sorted
      val (result, last) = sorted.tail.foldLeft((Seq.empty[String], (sorted.head, sorted.head))) {
        case ((acc, (start, end)), curr) =>
          if curr == end + 1 then (acc, (start, curr))
          else (acc :+ formatInterval(start, end), (curr, curr))
      }
      result :+ formatInterval(last._1, last._2)

  private def formatInterval(start: Int, end: Int): String =
    if start == end then s"$start"
    else s"$start-$end"
