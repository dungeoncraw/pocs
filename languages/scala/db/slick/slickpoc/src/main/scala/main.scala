
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
    val coffees: TableQuery[Coffees] = TableQuery[Coffees]
    val setup: DBIO[Unit] = DBIO.seq(
      (providers.schema ++ coffees.schema).create,
      providers += (101, "Acme", "123 Main St", "Anytown", "CA", "90210" ),
      providers += (102, "Nops", "456 Main St", "Anytown", "CA", "90210")
    )
    val setupFuture: Future[Unit] = db.run(setup)
    val f = setupFuture.flatMap{ _ =>
      val insertAction: DBIO[Option[Int]] = coffees ++= Seq(
        ( "Latte", 101, 2.50, 100, 250),
        ("Colombian", 102, 3.00, 100, 300),
        ("Espresso", 102, 1.50, 100, 150),
        ("Cappuccino", 101, 2.00, 100, 200),
        ("French_Roast", 102, 2.50, 100, 250),
      )
      db.run(insertAction)
      val filterQuery: Query[Provider, (Int, String, String, String, String, String), Seq] =
        // === is slick DSL for SQL equality
        providers.filter(_.zip === "90210")

      db.run(filterQuery.result).map(println)
      println("Getting with join\n")
      val qjoin = for {
        c <- coffees if c.price < 9.0
        // === for equality in slick
        s <- providers if s.id === c.providerId
      } yield (c.name, c.price, c.providerId, s.name, s.id )

      db.stream(qjoin.result).foreach(println)

      println("Getting with join but prices different than 2.00\n")
      val qexclusive = for {
        // =!= for getting values different from 2.00
        c <- coffees if c.price =!= 2.00
        s <- providers if s.id === c.providerId
      } yield (c.name, c.price, c.providerId, s.name, s.id )

      db.stream(qexclusive.result).foreach(println)
    }
    Await.result(f, Duration.Inf)
  }
  finally db.close()


