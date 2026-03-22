import com.google.gson.Gson

case class User(name: String, nickname: Option[String])
case class Team(members: List[String])
case class Config(host: String = "localhost", port: Int = 8080)
case class Person(name: String, age: Int)

@main
def main(): Unit = {
  val gson = new Gson()

  val u1 = User("Alice", Some("ali"))
  val u2 = User("Bob", None)
  // Some is treated as null in JSON
  println(gson.toJson(u1))
  println(gson.toJson(u2))

  val team = Team(List("Ava", "Ben"))
  // Scala list is not recognized as a JSON array, so print null
  println(gson.toJson(team))


  val json = """{}"""
  // Default values in case class are ignored, so host is null, and port is 0
  val config = gson.fromJson(json, classOf[Config])
  println(config)

  // this works as expected
  val person = Person("Ava", 21)
  val jsonPerson = gson.toJson(person)

  println(jsonPerson)

  val parsed = gson.fromJson(jsonPerson, classOf[Person])
  println(parsed)
}

