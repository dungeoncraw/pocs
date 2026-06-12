import models.{Day, Task, TaskType, TimeSlot}

@main
def main(): Unit = {
  val allSlots =
    for
      day <- Day.all
      hour <- 8 to 17
    yield TimeSlot(day, hour)
  println("================== TIME SLOTS ==================")
  println(allSlots)


  val tasks =
    Vector(
      Task(
        id = "class-monday",
        title = "Math class",
        kind = TaskType.Class,
        durationHours = 1,
        priority = 5,
        fixedStart = Some(TimeSlot(Day.Monday, 9)),
        deadline = None,
        guardDays = 0,
        canSplit = false
      ),
      Task(
        id = "seminar-tuesday",
        title = "Pedagogy seminar",
        kind = TaskType.Seminar,
        durationHours = 2,
        priority = 4,
        fixedStart = Some(TimeSlot(Day.Tuesday, 14)),
        deadline = None,
        guardDays = 0,
        canSplit = false
      ),
      Task(
        id = "apply-test",
        title = "Apply algebra test",
        kind = TaskType.ApplyTest,
        durationHours = 1,
        priority = 5,
        fixedStart = Some(TimeSlot(Day.Wednesday, 9)),
        deadline = None,
        guardDays = 0,
        canSplit = false
      ),
      Task(
        id = "correct-test",
        title = "Correct algebra test",
        kind = TaskType.CorrectTest,
        durationHours = 4,
        priority = 5,
        fixedStart = None,
        deadline = Some(TimeSlot(Day.Friday, 17)),
        guardDays = 2,
        canSplit = true
      ),
      Task(
        id = "correct-homework",
        title = "Correct homework",
        kind = TaskType.CorrectHomework,
        durationHours = 2,
        priority = 3,
        fixedStart = None,
        deadline = Some(TimeSlot(Day.Thursday, 17)),
        guardDays = 1,
        canSplit = true
      ),
      Task(
        id = "prepare-lesson",
        title = "Prepare next math lesson",
        kind = TaskType.PrepareLesson,
        durationHours = 2,
        priority = 5,
        fixedStart = None,
        deadline = Some(TimeSlot(Day.Wednesday, 9)),
        guardDays = 1,
        canSplit = false
      ),
      Task(
        id = "admin-emails",
        title = "Answer school emails",
        kind = TaskType.Admin,
        durationHours = 1,
        priority = 1,
        fixedStart = None,
        deadline = Some(TimeSlot(Day.Monday, 17)),
        guardDays = 0,
        canSplit = true
      )
    )
  println("================== TASKS ==================")
  println(tasks)
  val schedule = new services.Scheduler().schedule(tasks, allSlots)
  println("================== SCHEDULE ==================")
  println(schedule.scheduled)
  println(schedule.unscheduled)
}

