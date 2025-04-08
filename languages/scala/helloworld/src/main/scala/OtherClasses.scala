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