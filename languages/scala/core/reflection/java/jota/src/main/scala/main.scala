import java.lang.reflect.{Field, Method, Constructor}

class Person(private var name: String, val age: Int) {
  def sayHello(): Unit = {
    println(s"Hello, my name is $name and I am $age years old.")
  }

  private def mySecret(): String = {
    s"This is a secret message for $name."
  }
}

@main
def main(): Unit = {
  try {
    val personClass: Class[Person] = classOf[Person]
    println(s"Class name: ${personClass.getName}")
    val constructor: Constructor[Person] = personClass.getConstructor(classOf[String], classOf[Int])
    val personInstance: Person = constructor.newInstance("John Doe", 30)
    println("Created Person instance using reflection.")
    personInstance.sayHello()

    val nameField: Field = personClass.getDeclaredField("name")
    nameField.setAccessible(true)
    println(s"Original name (via reflection): ${nameField.get(personInstance)}")

    nameField.set(personInstance, "Jane Smith")
    println("Modified name using reflection.")
    personInstance.sayHello()
    // this is very dangerous, but it works!
    val privateMethod: Method = personClass.getDeclaredMethod("mySecret")
    privateMethod.setAccessible(true)
    val secretMessage = privateMethod.invoke(personInstance)
    println(s"Invoked private method: $secretMessage")

    println("\nAll methods in Person class:")
    personClass.getDeclaredMethods.foreach { m =>
      println(s"  - ${m.getName} (returns ${m.getReturnType.getSimpleName})")
    }

  } catch {
    case e: Exception =>
      e.printStackTrace()
  }
}
