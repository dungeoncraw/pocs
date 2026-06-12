import models.{Day, Task, TaskType, TimeSlot}
import scala.util.Random

def generateRandomTasks(seed: Int): Vector[Task] = {
  val random = new Random(seed)
  val taskTypes = TaskType.values.toVector
  val days = Day.all

  (1 to seed).map { i =>
    val kind = taskTypes(random.nextInt(taskTypes.size))
    val isFixed = random.nextBoolean()
    val duration = 1 + random.nextInt(3)
    val priority = 1 + random.nextInt(5)
    
    val (fixedStart, deadline) = if (isFixed) {
      val day = days(random.nextInt(days.size))
      val hour = 8 + random.nextInt(18 - 8 - duration + 1)
      (Some(TimeSlot(day, hour)), None)
    } else {
      val day = days(random.nextInt(days.size))
      val hour = 17
      (None, Some(TimeSlot(day, hour)))
    }

    Task(
      id = s"task-$i",
      title = s"Random Task $i ($kind)",
      kind = kind,
      durationHours = duration,
      priority = priority,
      fixedStart = fixedStart,
      deadline = deadline,
      guardDays = random.nextInt(3),
      canSplit = random.nextBoolean()
    )
  }.toVector
}

@main
def main(): Unit = {
  val allSlots =
    for
      day <- Day.all
      hour <- 8 to 17
    yield TimeSlot(day, hour)
  
  val seed = 1 + new Random().nextInt(100)
  println(s"Using random seed: $seed")
  
  val tasks = generateRandomTasks(seed)
  
  println("================== TASKS ==================")
  tasks.foreach(println)
  
  val schedule = new services.Scheduler().schedule(tasks, allSlots)
  services.ReportService.printSchedule(schedule)
}

