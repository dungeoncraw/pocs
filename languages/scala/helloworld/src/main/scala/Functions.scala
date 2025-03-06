@main
def functions(): Unit = {
  println(multiply(2))
  println(multiply(2.2))
  println(multiply(2,3))
  sayHello("John")
  sayHello(List("Mary", "Jane"))
  sayHello("Peter", "Parker", "Jesus")
  val listUser = List("Amelia", "Maria", "Isadora")
  // splat operator
  sayHello(listUser*)
}

// override
def multiply(a: Int): Int = a * 2
def multiply(a: Double): Double = a * 2
def multiply(a: Int, b: Int): Int = a * b

def sayHello(name: String): Unit = println(s"Hello $name")
def sayHello(people: Iterable[String]): Unit =
  for(person <- people)
    println(s"Hello $person")

def sayHello(names: String*): Unit =
  for(name <- names)
    println(s"Hello $name")