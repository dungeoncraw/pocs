import scala.util.Random

@main
def traits(): Unit = {
  val oven = getOven()
  oven.turnOn()
  oven.bake(100)
  oven.turnOff()
  val couch: Couch = Couch()
  couch.sit()
  couch.sleep()

  val toby: Labrador = Labrador()
  toby.play()
  toby.eat()
  toby.breathe()
  // case class data is immutable
  val u1 = User("Toby", 2, "<EMAIL>", "password")
  val u2 = User("Toby", 2, "<EMAIL>", "password")
  // case class print actual data instead of memory address
  println(u1.toString)
  val r1 = RegularUser("Toby", 2, "<EMAIL>", "password")
  val r2 = RegularUser("Toby", 2, "<EMAIL>", "password")
  println(r1.toString)
  // this must be true as data is the same
  println(u1 == u2)
  // this must be false as the memory address isn't the same
  println(r1 == r2)

  val u3 = u1.copy(age = 3)
  println(u3)
}



trait Oven:
  val temperature: Int
  // Traits can implement function
  def bake(temp: Int): Unit =
    println(s"Baking... at $temp")

  def turnOn(): Unit
  def turnOff(): Unit

class LargerOven extends Oven:
  override val temperature: Int = 180

  override def turnOff(): Unit =
    println("Turning off a larger oven...")

  override def turnOn(): Unit =
    println("Turning on a larger oven...")

class SmallerOven extends Oven:
  override val temperature: Int = 180

  override def turnOff(): Unit =
    println("Turning off a smaller oven...")

  override def turnOn(): Unit =
    println("Turning on a smaller oven...")

def getOven(): Oven =
  if (Random.nextInt(2) == 0)
    LargerOven()
  else
    SmallerOven()

trait PlaceToSit:
  def sit(): Unit

trait PlaceToSleep:
  def sleep(): Unit

// class can implement two traits at same time
class Couch extends PlaceToSit, PlaceToSleep:
  override def sit(): Unit =
    println("Sitting on a couch")

  override def sleep(): Unit =
    println("Sleeping on a couch")

// trait can inherit from another trait
trait Animal:
  def eat(): Unit

trait Mammal extends Animal:
  def breathe(): Unit

trait Puppy extends Mammal:
  def play(): Unit

class Labrador extends Puppy:
  override def breathe(): Unit =
    println("Labrador breathes")

  override def eat(): Unit =
    println("Labrador eats")

  override def play(): Unit =
    println("Labrador plays")

case class User(
               name: String,
               age: Int,
               email: String,
               password: String,
             )

class RegularUser(
                   name: String,
                   age: Int,
                   email: String,
                   password: String,
                 )