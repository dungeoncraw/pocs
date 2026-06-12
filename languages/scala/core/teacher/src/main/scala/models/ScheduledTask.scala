package models

case class ScheduledTask(
                          task: Task,
                          slots: Vector[TimeSlot]
                        )