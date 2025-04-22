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

    val maxPrice: Rep[Option[Double]] = coffees.map(_.price).max
    // maxPrice is now Rep.forNode[Option[Double']]
    val maxPriceResult: Future[Option[Double]] = db.run(maxPrice.result)
    maxPriceResult.onComplete {
      // TODO this is showing NONE instead of value, need to handle futures
      case Success(v) => println(s"Max price: $v")
      case Failure(e) => e.printStackTrace()
    }

    val q = for (c <- coffees) yield c.name
    val a = q.result
    val f2: Future[Seq[String]] = db.run(a)
    f2.onComplete {
      case Success(v) => println(s"Result: $v")
      case Failure(e) => e.printStackTrace()
    }
  }
  finally db.close()


