import zio.*
import scala.collection.mutable.ListBuffer

@main
def run(): Unit =
  val tasks = ListBuffer.empty[String]

  val program =
    for
      _ <- Console.printLine("Enter tasks one per line. Type 'done' to finish.")
      _ <- addTasks(tasks)
      _ <- Console.printLine(s"You entered ${tasks.size} task(s):")
      _ <- ZIO.foreachDiscard(tasks.toList)(task => Console.printLine(s"- $task"))
    yield ()

  Unsafe.unsafe { implicit unsafe =>
    Runtime.default.unsafe.run(program).getOrThrowFiberFailure()
  }

def addTasks(tasks: ListBuffer[String]): ZIO[Any, Throwable, Unit] =
  for
    input <- Console.readLine
    _ <-
      if input.trim.equalsIgnoreCase("done") then
        ZIO.unit
      else if input.trim.isEmpty then
        Console.printLine("Task cannot be empty. Try again.") *> addTasks(tasks)
      else
        tasks += input.trim
        Console.printLine(s"Added: ${input.trim}") *> addTasks(tasks)
  yield ()