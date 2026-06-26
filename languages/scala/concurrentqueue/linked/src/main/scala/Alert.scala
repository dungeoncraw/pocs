import java.util
import java.util.concurrent.{ConcurrentLinkedQueue, CountDownLatch, Executors, PriorityBlockingQueue, TimeUnit}
import java.util.Queue
import scala.util.Random

enum AlertPriority extends Ordered[AlertPriority]:
  case Low, Medium, High, Critical

  def compare(that: AlertPriority): Int =
    this.ordinal.compare(that.ordinal)

case class Alert(message: String, priority: AlertPriority = AlertPriority.Medium) extends Comparable[Alert]:
  override def compareTo(other: Alert): Int =
    other.priority.compare(this.priority)

object QueueCases:
  def runMultiThreaded(queueName: String, queue: util.Queue[Alert], totalMessages: Int): Unit = {
    val numProducers = Random.nextInt(20) + 1
    val numConsumers = Random.nextInt(20) + 1
    val latch = new CountDownLatch(numProducers)
    val executor = Executors.newFixedThreadPool(numProducers + numConsumers)

    for (i <- 1 to numProducers) {
      executor.execute(() => {
        try {
          for (j <- 1 to totalMessages / numProducers) {
            val priority = AlertPriority.fromOrdinal((i + j) % 4)
            val alert = Alert(s"Producer $i message $j", priority)
            queue.add(alert)
            Thread.sleep(10)
          }
        } finally {
          latch.countDown()
        }
      })
    }

    for (i <- 1 to numConsumers) {
      executor.execute(() => {
        var processedCount = 0
        while (latch.getCount > 0 || !queue.isEmpty) {
          val alert = queue.poll()
          if (alert != null) {
            println(s"[$queueName] Consumer $i processed: [${alert.priority}] ${alert.message}")
            processedCount += 1
          } else {
            Thread.sleep(20)
          }
        }
      })
    }

    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.MINUTES)
  }

@main
def main(): Unit = {
  val fifoQueue = new ConcurrentLinkedQueue[Alert]()
  QueueCases.runMultiThreaded("FIFO", fifoQueue, Random.nextInt(200) + 1)

  val priorityQueue = new PriorityBlockingQueue[Alert]()
  QueueCases.runMultiThreaded("Priority", priorityQueue, 12)
}
