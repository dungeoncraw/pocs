
import org.json4s.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import java.time.LocalDate
import org.json4s.JsonDSL.*
import org.json4s.native.JsonMethods.*

case class Address(city: String, street: String)
case class User(name: String, age: Int, address: Option[Address], birthDate: LocalDate, tags: List[String])

// Custom Serializer
object LocalDateSerializer extends CustomSerializer[LocalDate](format => (
  {
    case JString(s) => LocalDate.parse(s)
    case JNull => null
  },
  {
    case d: LocalDate => JString(d.toString)
  }
))

@main
def main(): Unit = {
  implicit val formats: Formats = DefaultFormats + LocalDateSerializer

  val user = User(
    name = "Alice",
    age = 30,
    address = Some(Address("New York", "5th Ave")),
    birthDate = LocalDate.of(1994, 5, 15),
    tags = List("scala", "json4s", "developer")
  )

  println("--- 1. Serialization & Deserialization ---")
  val jsonString = write(user)
  println(s"Serialized User: $jsonString")

  // Deserialization
  val bobJson = """{"name":"Bob","age":25,"birthDate":"1999-12-31","tags":["coding"]}"""
  val bob = read[User](bobJson)
  println(s"Deserialized User: $bob")

  println("\n--- 2. JSON DSL & Methods ---")

  val jsonDSL =
    ("user" ->
      ("id" -> 1) ~
      ("username" -> "char_lie") ~
      ("active" -> true) ~
      ("roles" -> List("admin", "editor"))
    )

  val renderedJson = compact(render(jsonDSL))
  println(s"DSL Generated: $renderedJson")

  println("\n--- 3. Querying & Extraction ---")
  val parsedJson = parse(renderedJson)

  // XPath-like querying
  val username = (parsedJson \ "user" \ "username").extract[String]
  println(s"Extracted username: $username")

  val roles = (parsedJson \ "user" \ "roles").extract[List[String]]
  println(s"Extracted roles: $roles")

  // Find elements
  val activeStatus = (parsedJson \\ "active").extract[Boolean]
  println(s"Found active status: $activeStatus")

  println("\n--- 4. Transformation ---")
  // Transform: Capitalize all field names
  val capitalizedJson = parsedJson.transformField {
    case (name, value) => (name.capitalize, value)
  }
  println(s"Transformed (Capitalized Keys): ${compact(render(capitalizedJson))}")
}

