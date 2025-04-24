import slick.jdbc.H2Profile.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}

case class Sale(id: Option[Int], clientName: String, providerId: Int, coffeeId: Int, payment: String )

class Provider(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "PROVIDER") {
  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("NAME")
  def street: Rep[String] = column[String]("STREET")
  def city: Rep[String] = column[String]("CITY")
  def state: Rep[String] = column[String]("STATE")
  def zip: Rep[String] = column[String]("ZIP")
  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(Int, String, String, String, String, String)] = (id, name, street, city, state, zip)
}

class Coffees(tag: Tag) extends Table[(Int, String, Int, Double, Int, Int)](tag, "COFFEES") {
  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("NAME")
  def providerId: Rep[Int] = column[Int]("PROVIDER_ID")
  def price: Rep[Double] = column[Double]("PRICE")
  def sales: Rep[Int] = column[Int]("SALES")
  def total: Rep[Int] = column[Int]("TOTAL")
  def * : ProvenShape[(Int, String, Int, Double, Int, Int)] = (id, name, providerId, price, sales, total)

  def provider = foreignKey("PROVIDER_FK", providerId, TableQuery[Provider])(_.id)
}

class Sales(tag: Tag) extends Table[Sale](tag, "SALES") {
  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def clientName: Rep[String] = column[String]("NAME")
  def providerId: Rep[Int] = column[Int]("PROVIDER_ID")
  def coffeeId: Rep[Int] = column[Int]("COFFEE_ID")
  def payment: Rep[String] = column[String]("PAYMENT")
  //  mapTo is a slick method for getting the table type
  def * = (id.?, clientName, providerId, coffeeId, payment).mapTo[Sale]

  def idx = index("SALES_IDX", (clientName, providerId, coffeeId), unique = true)

  def provider = foreignKey("SALE_PROVIDER_FK", providerId, TableQuery[Provider])(_.id)
  // configuration of FK with cascade delete
  def coffee = foreignKey("COFFEE_FK", coffeeId, TableQuery[Coffees])(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

}