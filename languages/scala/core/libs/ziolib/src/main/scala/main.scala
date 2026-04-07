import zio.*
import scala.collection.mutable.ListBuffer

object Main extends ZIOAppDefault:
  def run: ZIO[ZIOAppArgs & Scope, Any, Any] =
    val tasks = ListBuffer.empty[String]

    for
      _ <- Console.printLine("Enter tasks one per line. Type 'done' to finish.")
      _ <- addTasks(tasks)
      _ <- Console.printLine(s"You entered ${tasks.size} task(s):")
      _ <- ZIO.foreachDiscard(tasks.toList)(task => Console.printLine(s"- $task"))
      _ <- computeParallel()
      _ <- computeQueue(tasks)
      _ <- promiseAwait()
      _ <- clockSleep()
    yield ()

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

def computeParallel(): ZIO[Any, Throwable, Unit] =
  for
    // create a mutable ref to update in multiple fibers
    ref <- Ref.make(0)
    // create 100 fibers that update the ref 1000 times each
    fibers <- ZIO.foreachPar(1 to 100) { _ =>
      ZIO.foreachDiscard(1 to 1000) { _ =>
        // update the ref
        ref.update(_ + 1)
      }
      // fork to start the computation in parallel
    }.fork
    // just wait the fibers to finish
    _ <- fibers.join
    //get the final value 100000
    value <- ref.get
    _ <- Console.printLine(s"Final value: $value")
  yield ()

def computeQueue(tasks: ListBuffer[String]): ZIO[Any, Throwable, Unit] =
  def worker(name: String, queue: Queue[String]): ZIO[Any, Throwable, Unit] =
    queue.take.flatMap { value =>
      Console.printLine(s"$name got $value")
    }.forever

  for
    queue <- Queue.unbounded[String]

    fiber1 <- worker("worker-1", queue).fork
    fiber2 <- worker("worker-2", queue).fork
    fiber3 <- worker("worker-3", queue).fork

    _ <- ZIO.foreachDiscard(tasks)(n => queue.offer(n))
    _ <- ZIO.sleep(500.millis)
    // stop the fibers as the workers run forever
    _ <- fiber1.interrupt
    _ <- fiber2.interrupt
    _ <- fiber3.interrupt
  yield ()

def promiseAwait(): ZIO[Any, Throwable, Unit] =
  def worker(name: String, ref: Ref[Int], startSignal: Promise[Nothing, Unit]): ZIO[Any, Throwable, Unit] =
    for
      _ <- Console.printLine(s"$name is ready and waiting...")
      multiplier <- Random.nextIntBounded(20).map(_ + 2)
      _ <- Console.printLine(s"$name will multiply the value by $multiplier")
      _ <- startSignal.await
      _ <- ref.update(_ * multiplier)
      _ <- Console.printLine(s"$name started working")
    yield ()

  for
    ref <- Random.nextIntBounded(199).map(_ + 2).flatMap(Ref.make)
    // need to read the value, as Ref.make create an effect
    initialValue <- ref.get
    _ <- Console.printLine(s"Initial value: ${initialValue}")
    // promise for signaling the start of the workers
    startSignal <- Promise.make[Nothing, Unit]

    fiber1 <- worker("worker-1", ref, startSignal).fork
    fiber2 <- worker("worker-2", ref,  startSignal).fork
    fiber3 <- worker("worker-3", ref,  startSignal).fork

    _ <- ZIO.sleep(2.seconds)
    _ <- Console.printLine("Releasing all workers now...")
    // signal all workers to start working
    _ <- startSignal.succeed(())

    _ <- fiber1.join
    _ <- fiber2.join
    _ <- fiber3.join
    finalValue <- ref.get
    _ <- Console.printLine(s"Final value: ${finalValue}")
  yield ()

def clockSleep(): ZIO[Any, Throwable, Unit] =
  for
    sleeper <- (
      for
        _ <- Console.printLine("Sleep thread start and wait")
        _ <- Clock.sleep(3.second)
        _ <- Console.printLine("Sleep thread finish the process")
      yield ()
    ).fork

    worker <- (
      for
        _ <- Console.printLine("Worker thread start")
        _ <- Clock.sleep(500.millis)
        _ <- Console.printLine("Worker doing another thing")
        _ <- Clock.sleep(500.millis)
        _ <- Console.printLine("Worker thread end")
      yield ()
    ).fork

    _ <- sleeper.join
    _ <- worker.join
  yield ()