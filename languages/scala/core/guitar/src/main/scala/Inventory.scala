class Inventory:
  private var parts: Map[GuitarPart, Int] = Map(
    BodyWood.AlderBody -> 5,
    BodyWood.MahoganyBody -> 3,
    NeckWood.MapleNeck -> 6,
    NeckWood.RosewoodNeck -> 4,
    Pickup.SingleCoil -> 10,
    Pickup.Humbucker -> 8,
    Bridge.Fixed -> 5,
    Bridge.Tremolo -> 4,
    Finish.Black -> 6,
    Finish.Sunburst -> 5,
    GuitarOS.PassiveAnalog -> 10,
    GuitarOS.SmartToneOS -> 3
  )

  def show(): Unit =
    println("\nInventory:")
    parts.foreach { case (part, quantity) =>
      println(s"- ${part.label}: $quantity")
    }

  def quantity(part: GuitarPart): Int =
    parts.getOrElse(part, 0)

  def has(part: GuitarPart): Boolean =
    parts.getOrElse(part, 0) > 0

  def use(part: GuitarPart): Boolean =
    if has(part) then
      parts = parts.updated(part, parts(part) - 1)
      true
    else
      false
