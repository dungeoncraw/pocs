import scala.util.Random

@main
def traits(): Unit = {
  val oven = getOven()
  oven.turnOn()
  oven.bake(100)
  oven.turnOff()
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