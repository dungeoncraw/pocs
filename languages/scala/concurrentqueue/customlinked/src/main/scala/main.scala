import java.util.concurrent.{CountDownLatch, Executors}
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration.*

@main
def main(): Unit =
  singleThreadedCase()
  println()
  concurrentCase()

def singleThreadedCase(): Unit =
  val queue = new CustomConcurrentLinkedQueue[String]

  println(s"isEmpty : ${queue.isEmpty}")
  println(s"poll: ${queue.poll()}")
  println(s"peek: ${queue.peek()}")

  queue.offer("a")
  queue.offer("b")
  queue.offer("c")

  println(s"size after offers: ${queue.size}")
  println(s"peek: ${queue.peek()}")
  for i <- 1 to 4 do println(s"poll: ${queue.poll()}")
  println(s"isEmpty: ${queue.isEmpty}")

  try
    queue.offer(null)
  catch case e: NullPointerException => println(s"null rejected: ${e.getMessage}")

def concurrentCase(): Unit =
  println("Concurrent steps")
  val queue = new CustomConcurrentLinkedQueue[Int]
  val producerCount = 4
  val consumerCount = 4
  val perProducer = 25_000
  val totalToProduce = producerCount * perProducer

  val produced = new AtomicInteger(0)
  val consumed = new AtomicInteger(0)
  val sumOut   = new AtomicInteger(0)
  val expectedSum = (1 to totalToProduce).sum

  val pool = Executors.newFixedThreadPool(producerCount + consumerCount)
  val ready = new CountDownLatch(producerCount + consumerCount)
  val start = new CountDownLatch(1)
  val done  = new CountDownLatch(producerCount + consumerCount)

  for p <- 0 until producerCount do
    pool.submit(new Runnable:
      def run(): Unit =
        ready.countDown()
        start.await()
        val from = p * perProducer + 1
        val to = from + perProducer - 1
        var i = from
        while i <= to do
          queue.offer(i)
          produced.incrementAndGet()
          i += 1
        done.countDown()
    )

  for _ <- 0 until consumerCount do
    pool.submit(new Runnable:
      def run(): Unit =
        ready.countDown()
        start.await()
        while consumed.get() < totalToProduce do
          queue.poll() match
            case Some(v) =>
              sumOut.addAndGet(v)
              consumed.incrementAndGet()
            case None =>
              Thread.`yield`()
        done.countDown()
    )

  ready.await()
  val t0 = System.nanoTime()
  start.countDown()
  done.await()
  val elapsed = (System.nanoTime() - t0).nanos

  pool.shutdown()

