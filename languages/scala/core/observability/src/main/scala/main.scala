package observability
import ObservabilityStore.*

import scala.util.Random


@main
def main(): Unit = {
  val db = new DbLayer()
  val service = new ServiceLayer(db)
  val controller = new ControllerLayer(service)
  
  for (i <- 1 to Random.nextInt(100)) {
    val result = controller.handleRequest(s"user-$i")
    println(s"Request $i Result: $result")
  }

  report()
}
