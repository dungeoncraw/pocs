
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import taskframework.TaskPool

@main
def main(): Unit = {
  println("Starting")
  
  val pool = new TaskPool(3)

  for (i <- 1 to 10) {
    pool.submit {
      val threadName = Thread.currentThread().getName
      println(s"Task $i is running on thread $threadName")
      Thread.sleep(500)
      println(s"Task $i completed")
    }
  }

  println("Tasks submitted, waiting.")
  Thread.sleep(3000)

  pool.shutdown()
}

