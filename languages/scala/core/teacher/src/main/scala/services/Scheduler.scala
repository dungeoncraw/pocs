package services

import models.{Day, Schedule, ScheduledTask, Task, TimeSlot}

class Scheduler:
  def schedule(tasks: Vector[Task], allSlots: Vector[TimeSlot]): Schedule =
    val fixedTasks = tasks.filter(_.fixedStart.isDefined)
    
    var scheduled = Vector.empty[ScheduledTask]
    var unscheduled = Vector.empty[Task]

    for task <- fixedTasks do
      getFixedTask(task) match
        case Some(scheduledTask) if isValid(scheduledTask) =>
          scheduled = scheduled :+ scheduledTask
        case _ =>
          unscheduled = unscheduled :+ task

    val flexible =
      tasks.filter(_.fixedStart.isEmpty)

    for task <- flexible do
      val free =
        freeSlots(allSlots, scheduled)

      findSlots(task, free) match
        case Some(slots) =>
          scheduled = scheduled :+ ScheduledTask(task, slots)

        case None =>
          unscheduled = unscheduled :+ task

    Schedule(scheduled, unscheduled)

  private def getFixedTask(task: Task): Option[ScheduledTask] =
    task.fixedStart.map { start =>
      val slots =
        (0 until task.durationHours).map { offset =>
          TimeSlot(start.day, start.hour + offset)
        }.toVector

      ScheduledTask(task, slots)
    }

  private def isValid(scheduledTask: ScheduledTask): Boolean =
    protectedDeadline(scheduledTask.task) match
      case Some(deadline) =>
        scheduledTask.slots.forall(_.index <= deadline.index)
      case None =>
        true

  private def freeSlots(
                         allSlots: Vector[TimeSlot],
                         scheduled: Vector[ScheduledTask]
                       ): Vector[TimeSlot] =
    val occupied =
      scheduled.flatMap(_.slots).toSet

    allSlots
      .filterNot(occupied.contains)
      .sortBy(_.index)

  private def protectedDeadline(task: Task): Option[TimeSlot] =
    task.deadline.map { realDeadline =>
      val effectiveGuard = math.max(1, task.guardDays)
      val protectedDay =
        Day.minusWorkDays(realDeadline.day, effectiveGuard)

      TimeSlot(protectedDay, realDeadline.hour)
    }

  private def findSlots(
                         task: Task,
                         freeSlots: Vector[TimeSlot]
                       ): Option[Vector[TimeSlot]] =
    val safeSlots =
      protectedDeadline(task) match
        case Some(deadline) =>
          freeSlots.filter(_.index <= deadline.index)

        case None =>
          freeSlots

    if task.canSplit then
      if safeSlots.size >= task.durationHours then
        Some(safeSlots.take(task.durationHours))
      else
        None
    else
      safeSlots
        .sliding(task.durationHours)
        .find(isContinuous)
        .map(_.toVector)

  private def isContinuous(slots: Seq[TimeSlot]): Boolean =
    val sorted =
      slots.sortBy(_.index)

    sorted
      .zip(sorted.drop(1))
      .forall { case (a, b) =>
        b.index == a.index + 1
      }
