package com.tetokeguii.taxsystem
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import com.tetokeguii.taxsystem.api.HttpTaxRoutes
import com.tetokeguii.taxsystem.domain.service.DomainTaxService
import com.tetokeguii.taxsystem.infrastructure.config.DatabaseConfig
import com.tetokeguii.taxsystem.infrastructure.repository.PersistentTaxRuleRepository
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import scala.concurrent.duration.*

@main
def main(): Unit = {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "TaxSystem")
  implicit val executionContext: ExecutionContext = system.executionContext

  val database = DatabaseConfig.database
  val repository = new PersistentTaxRuleRepository(database)
  val service = new DomainTaxService(repository)
  val routes = new HttpTaxRoutes(service)

  repository.createSchema().onComplete {
    case Success(_) => system.log.info("Database schema initialized")
    case Failure(ex) => system.log.error("Failed to initialize database schema", ex)
  }

  val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(routes.routes)

  bindingFuture.onComplete {
    case Success(binding) =>
      val address = binding.localAddress
      system.log.info(s"Server online at http://${address.getHostString}:${address.getPort}/")
    case Failure(ex) =>
      system.log.error(s"Failed to bind HTTP endpoint, terminating system", ex)
      system.terminate()
  }
}

