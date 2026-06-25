package observability

import scala.util.Random

class DbLayer {
  private val observability = new Observability("DB")

  def query(id: String): String = observability.track("query") {
    Thread.sleep(50 + Random.nextInt(50))
    s"Data[$id]"
  }
}

class ServiceLayer(db: DbLayer) {
  private val observability = new Observability("Service")

  def process(id: String): String = observability.track("process") {
    val data = db.query(id)
    Thread.sleep(20 + Random.nextInt(30))
    s"Processed($data)"
  }
}

class ControllerLayer(service: ServiceLayer) {
  private val observability = new Observability("Controller")

  def handleRequest(id: String): String = observability.track("handleRequest") {
    val response = service.process(id)
    Thread.sleep(10 + Random.nextInt(20))
    response
  }
}
