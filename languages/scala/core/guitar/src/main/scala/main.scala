
@main
def main(): Unit =
  val inventory = Inventory()
  val factory = GuitarService(inventory)

  inventory.show()

  val customSpec = GuitarSpec(
    model = GuitarModel.CustomStrat,
    bodyWood = BodyWood.AlderBody,
    neckWood = NeckWood.MapleNeck,
    pickup = Pickup.SingleCoil,
    bridge = Bridge.Tremolo,
    finish = Finish.Sunburst,
    os = GuitarOS.SmartToneOS
  )

  println("\nBuilding custom guitar...")

  factory.build(customSpec) match
    case Right(guitar) =>
      println("\nGuitar created successfully!")
      println(s"Description: ${guitar.description}")

    case Left(error) =>
      println(error)

  inventory.show()
