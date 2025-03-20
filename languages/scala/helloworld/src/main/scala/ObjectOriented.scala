@main
def oop(): Unit =
  val postItNote = PostItNote()
  println(postItNote.message)
  postItNote.updateMessage("Just update internal values")
  println(postItNote.message)
  postItNote.updateMessage("Some new values")
  postItNote.printMessage()

  val car1 = Car("Ford", "Mustang", 250)
  val car2 = Car("Fiat", "Palio", 5000)
  val car3 = Car()
  val car4 = Car("Tesla", "Model 3")
  car1.start()
  car2.start()
  car3.start()
  car4.start()

class PostItNote:
  var message: String = "No Message"

  def updateMessage(message: String) =
    this.message = message

  def printMessage() =
    // could use this or variable name inside a class
    println(message)

class Car(brand: String, model: String, topSpeed: Int):

  // new constructor
  def this(brand: String, model: String) = this(brand, model, 0)
  def this() = this("Ford", "Mustang", 250)

  def start() = println(s"Car $brand started with top speed $topSpeed")