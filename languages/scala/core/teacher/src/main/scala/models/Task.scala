package models

case class Task(
                 id: String,
                 title: String,
                 kind: TaskType,
                 durationHours: Int,
                 priority: Int,
                 fixedStart: Option[TimeSlot],
                 deadline: Option[TimeSlot],
                 guardDays: Int,
                 canSplit: Boolean
               )