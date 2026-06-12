package models

case class Schedule(
                     scheduled: Vector[ScheduledTask],
                     unscheduled: Vector[Task]
                   )