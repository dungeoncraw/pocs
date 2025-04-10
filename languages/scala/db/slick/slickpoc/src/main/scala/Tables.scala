import slick.jdbc.H2Profile.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}

class Provider(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "PROVIDER") {
  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey)
  def name: Rep[String] = column[String]("NAME")
  def street: Rep[String] = column[String]("STREET")
  def city: Rep[String] = column[String]("CITY")
  def state: Rep[String] = column[String]("STATE")
  def zip: Rep[String] = column[String]("ZIP")
  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(Int, String, String, String, String, String)] = (id, name, street, city, state, zip)
}