import scala.util.Random

@main
def oop(): Unit =
  val postItNote = PostItNote()
  println(postItNote.message)
  println(postItNote.getSize())
  postItNote.updateMessage("Just update internal values")
  println(postItNote.getSize())
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

  val airplane = Airplane()
  airplane.up()
  airplane.down()
  airplane.down()

  def getContainer(): Container =
    if (Random.nextInt(2) % 2 == 0)
      Bottle()
    else
      Jug()

  for (i <- 1 to 10)
    val container = getContainer()
    container.open()
    container.close()

class PostItNote:
  var message: String = "No Message"
  // this is initialization of parameters
  println("PostItNote class created")
  printMessage()

  // only available for same package
  protected var color: String = "black"
  private var size: Int = 10

  private def changeSize(v: Int) =
    size = v

  def getSize() = size

  def updateMessage(message: String) =
    this.message = message
    changeSize(15)

  def printMessage() =
    // could use this or variable name inside a class
    println(message)

class Car(brand: String, model: String, topSpeed: Int):

  // new constructor
  def this(brand: String, model: String) = this(brand, model, 0)
  def this() = this("Ford", "Mustang", 250)

  def start() = println(s"Car $brand started with top speed $topSpeed")

class Airplane:
  import Airplane.*
  private val brand: String = "Boeing"
  private var altitude: Int = 1000

  def up() =
    altitude += 1000
    println(s"$brand ascending to: $altitude")

  def down() =
    if (checkDescending(altitude))
      altitude -= 1000
      println(s"$brand descending to: $altitude")

object Airplane:
  def checkDescending(altitude: Int) =
    if (altitude <= 1000)
      println("Can't go below 1000")
      false
    else
      println("Can go below 1000")
      true

// open to inheritance
open class Person(nationality: String):
  val hairColor: String = "black"
  val eyeColor: String = "blue"

  def speak(message: String) = println(s"Hello $message, I am $nationality")

//Constructor of inherited class
class Keeper(nationality: String) extends Person(nationality):
  override val hairColor = "blonde"

  override def speak(message: String) =
    val newMessage = s"New message: $message"
    super.speak(newMessage)

abstract class Container:
  def open() = println("Opening container")
  def close() = println("Closing container")

class Bottle extends Container:
  override def open() = println("Opening bottle")
  override def close() = println("Closing bottle")

class Jug extends Container:
  override def open() = println("Opening jug")
  override def close() = println("Closing jug")