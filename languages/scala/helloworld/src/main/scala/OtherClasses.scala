import scala.util.Random

@main
def otherClasses(): Unit =
  println(s"Hello ${Color.RED}")
  decide(Color.RED)
  decide(Color.GREEN)

  println(Color.valueOf("RED"))
  // position
  println(Color.GREEN.ordinal)
  println(Color.values.toList)
  println(Color.fromOrdinal(2))
  Color.RED.drawColor()
  println(Color.BLUE.hex)

  val plant = getPlant()
  plant match
    case _: Fruit => println("Fruit")
    case _: Vegetable => println("Vegetable")
    case _ => println("Unknown")

  val c = NestedCar()
  c.drive()

// enums can have constructor and methods!
enum Color(val hex: String):
  case RED extends Color("0xff0000")
  case GREEN extends Color("0x00ff00")
  case BLUE extends Color("0x0000ff")

  def drawColor(): Unit = println(s"Drawing color $hex")

def decide(color: Color): Unit =
  color match
    case Color.RED => println("You choose red")
    case Color.GREEN => println("You choose green")
    case Color.BLUE => println("You choose blue")

// sealed classes can only be accessed by the current package class
sealed class Plant

sealed class Fruit extends Plant

sealed class Vegetable extends Plant

class Apple extends Fruit

class Potato extends Vegetable

def getPlant(): Plant =
  if (Random.nextInt(100) % 2 == 0) new Apple() else new Potato()


class NestedCar:
  var speed = 100
  val engine = Engine()

  def drive(): Unit =
    engine.start()
    println(s"Driving at $speed km/h")

  class Engine:
    val power = 100
    // this is very dangerous, calling that way the nested class
    NestedCar.this.speed = 200
    def start(): Unit = println(s"Starting engine with power $power")