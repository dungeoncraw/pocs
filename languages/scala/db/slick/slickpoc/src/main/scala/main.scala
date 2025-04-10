
import slick.basic.DatabasePublisher
import slick.jdbc.H2Profile.api.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration}
import scala.concurrent.{Await, Future}

@main
def main(): Unit =
  val db = Database.forConfig("h2mem")
  try {
    val providers: TableQuery[Provider] = TableQuery[Provider]
    val setup: DBIO[Unit] = DBIO.seq(
      providers.schema.create,
      providers += (101, "Acme", "123 Main St", "Anytown", "CA", "90210" ),
      providers += (102, "Nops", "456 Main St", "Anytown", "CA", "90210")
    )
    val setupFuture: Future[Unit] = db.run(setup)
    val f = setupFuture.flatMap{ _ =>
      val filterQuery: Query[Provider, (Int, String, String, String, String, String), Seq] =
        // === is slick DSL for SQL equality
        providers.filter(_.zip === "90210")

      println("Generated SQL: \n"+filterQuery.result.statements)
      db.run(filterQuery.result).map(println)
    }
    Await.result(f, Duration.Inf)
  }
  finally db.close()


