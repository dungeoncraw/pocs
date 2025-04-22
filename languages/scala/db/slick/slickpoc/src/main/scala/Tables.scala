import slick.jdbc.H2Profile.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}

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

class Coffees(tag: Tag) extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {
  def name: Rep[String] = column[String]("NAME")
  def providerId: Rep[Int] = column[Int]("PROVIDER_ID")
  def price: Rep[Double] = column[Double]("PRICE")
  def sales: Rep[Int] = column[Int]("SALES")
  def total: Rep[Int] = column[Int]("TOTAL")
  def * : ProvenShape[(String, Int, Double, Int, Int)] = (name, providerId, price, sales, total)

  def provider = foreignKey("PROVIDER_FK", providerId, TableQuery[Provider])(_.id)
}