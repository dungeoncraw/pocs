package taskframework

import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}
import java.util.concurrent.atomic.AtomicBoolean

class TaskPool(numberOfThreads: Int) {
  private val taskQueue: BlockingQueue[Runnable] = new LinkedBlockingQueue[Runnable]()
  private val workers = (1 to numberOfThreads).map(_ => new Worker())
  private val isShutdown = new AtomicBoolean(false)

  workers.foreach(_.start())

  def submit(task: Runnable): Unit = {
    if (isShutdown.get()) {
      throw new IllegalStateException("TaskPool is shut down")
    }
    taskQueue.put(task)
  }

  def submit(f: => Unit): Unit = {
    submit(new Runnable {
      override def run(): Unit = f
    })
  }

  def shutdown(): Unit = {
    isShutdown.set(true)
    workers.foreach(_.interrupt())
  }

  private class Worker extends Thread {
    override def run(): Unit = {
      try {
        while (!Thread.currentThread().isInterrupted && !(isShutdown.get() && taskQueue.isEmpty)) {
          val task = taskQueue.take()
          try {
            task.run()
          } catch {
            case e: Exception => e.printStackTrace()
          }
        }
      } catch {
        case _: InterruptedException => // Worker stopping
      }
    }
  }
}
