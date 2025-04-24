import slick.jdbc.H2Profile.api.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Success, Failure}

@main
def main(): Unit =
  val db = Database.forConfig("h2mem")
  // also can use the URL for getting it like Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")
  try {
    val providers: TableQuery[Provider] = TableQuery[Provider]
    val coffees: TableQuery[Coffees] = TableQuery[Coffees]
    val sales: TableQuery[Sales] = TableQuery[Sales]
    val setup: DBIO[Unit] = DBIO.seq(
      (providers.schema ++ coffees.schema ++ sales.schema).create,
      providers += (101, "Acme", "123 Main St", "Anytown", "CA", "90210" ),
      providers += (102, "Nops", "456 Main St", "Anytown", "CA", "90210")
    )
    val setupFuture: Future[Unit] = db.run(setup)
    val f = setupFuture.flatMap{ _ =>
      // String, Int, Double, Int, Int
      val insertAction: DBIO[Option[Int]] = coffees ++= Seq(
        (1,  "Latte", 1, 2.50, 100, 250),
        (2, "Colombian", 2, 3.00, 100, 300),
        (3, "Espresso", 1, 1.50, 100, 150),
        (4, "Cappuccino", 1, 2.00, 100, 200),
        (5, "French_Roast", 2, 2.50, 100, 250),
      )
      db.run(insertAction)
      val insertSales: DBIO[Option[Int]] = sales ++= Seq(
        Sale(Some(1), "John", 1, 1, "Cash"),
        Sale(Some(2), "Jane", 2, 2, "Cash"),
        Sale(Some(3), "Jill", 1, 3, "Jullian"),
        Sale(Some(4), "Andy", 2, 4, "Donavan"),
      )
      db.run(insertSales)
    }
    Await.result(f, Duration.Inf)

    val filterQuery: Query[Provider, (Int, String, String, String, String, String), Seq] =
      // === is slick DSL for SQL equality
      providers.filter(_.zip === "90210")

    db.run(filterQuery.result).map(p => println(s"Providers: $p\n"))

    val filterCoffeeQuery: Query[Coffees, (Int, String, Int, Double, Int, Int), Seq] =
      // === is slick DSL for SQL equality
      coffees.filter(_.price < 3.00)

    db.run(filterCoffeeQuery.result).map(p => println(s"Coffees: $p\n"))

    println("Getting with join but prices different than 2.00\n")
    val qexclusive = for {
      // =!= for getting values different from 2.00
      c <- coffees if c.price =!= 2.00
      s <- c.provider
    } yield (c.name, c.price, c.providerId, s.name, s.id)

    db.run(qexclusive.result).foreach(t => println(s"Item exclusive join: $t\n"))

    println("Getting with join\n")
    val qjoin = for {
      c <- coffees if c.price < 3.0
      // === for equality in slick
      s <- c.provider
    } yield (c.name, c.price, c.providerId, s.name, s.id)

    db.run(qjoin.result).foreach(t => println(s"Item join: $t\n"))

  }
  finally db.close()


