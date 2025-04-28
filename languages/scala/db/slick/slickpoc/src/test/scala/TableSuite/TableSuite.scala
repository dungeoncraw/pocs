import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta._

class TablesSuite extends funsuite.AnyFunSuite with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val suppliers = TableQuery[Provider]
  val coffees = TableQuery[Coffees]

  var db: Database = _

  def createSchema() =
    db.run((suppliers.schema ++ coffees.schema).create).futureValue

  def insertSupplier(): Int =
    db.run(suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")).futureValue
  // which database config run
  before { db = Database.forConfig("h2mem") }

  test("Creating the Schema works") {
    createSchema()

    val tables = db.run(MTable.getTables).futureValue

    assert(tables.size == 2)
    assert(tables.count(_.name.name.equalsIgnoreCase("provider")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("coffees")) == 1)
  }

  test("Inserting a Supplier works") {
    createSchema()

    val insertCount = insertSupplier()
    assert(insertCount == 1)
  }

  test("Query Suppliers works") {
    createSchema()
    insertSupplier()
    val results = db.run(suppliers.result).futureValue
    assert(results.size == 1)
    assert(results.head._1 == 1)
  }

  after { db.close }
}