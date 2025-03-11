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

  // lambdas
  val myLambda = (a: Int) => a * 2
  myLambda(10)

  // HOC
  def myFunction(a: Int, byTwo: Int => Unit): Unit = byTwo(a)
  myFunction(10, (a: Int) => println(a * 2))

  val clients = List("John", "Mary", "Jane", "Lee")
  clients.foreach {
    client => println(s"Hello $client")
  }

  clients.filter(_.length < 4).foreach(println)

  val lengths = clients.map(_.length)
  println(lengths)

  // maxBy minBy get the first item that matches the predicate
  val max = clients.maxBy(_.length)
  println(max)
  val min = clients.minBy(_.length)
  println(min)
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