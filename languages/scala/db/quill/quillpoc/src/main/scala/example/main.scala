import io.getquill._
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZIOAppDefault, ZLayer}

import java.sql.SQLException

case class Person(name: String, age: Int)

class DataService(quill: Quill.Postgres[SnakeCase]) {
  import quill._
  def getPeople: ZIO[Any, SQLException, List[Person]] = run(query[Person])
}
object DataService {
  def getPeople: ZIO[DataService, SQLException, List[Person]] =
    ZIO.serviceWithZIO[DataService](_.getPeople)

  val live = ZLayer.fromFunction(new DataService(_))
}

/**
 * Simple example of Quill using the jdbc-zio context
 */
object Main extends ZIOAppDefault {
  override def run = {
    // Configuration to connect to PostgreSQL
    val dataSourceLayer = Quill.DataSource.fromPrefix("myDbConfig")

    DataService.getPeople
      .provide(
        DataService.live,
        Quill.Postgres.fromNamingStrategy(SnakeCase),
        dataSourceLayer
      )
      .debug("Results")
      .exitCode
  }
}