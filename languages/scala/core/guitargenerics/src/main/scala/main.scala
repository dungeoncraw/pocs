@main
def main(): Unit =

  val electricDefaultSpecs = GuitarSpecs(
    guitarType = GuitarType.Electric,
    bodyWood = Wood.Alder,
    neckWood = Wood.Maple,
    fretboardWood = Wood.Rosewood,
    pickups = List(Pickup.SingleCoil, Pickup.SingleCoil, Pickup.Humbucker),
    bridge = Bridge.Tremolo,
    finish = Finish.Gloss,
    stringGauge = StringGauge.Light,
    numberOfFrets = 22,
    leftHanded = false
  )

  val stratModel = GuitarModel(
    name = "Awesome Guitar",
    basePrice = BigDecimal(1200),
    defaultSpecs = electricDefaultSpecs
  )

  val customSpecs = GuitarSpecs(
    guitarType = GuitarType.Acoustic,
    bodyWood = Wood.Alder,
    neckWood = Wood.Rosewood,
    fretboardWood = Wood.Mahogany,
    pickups = List(Pickup.SingleCoil, Pickup.P90),
    bridge = Bridge.Tremolo,
    finish = Finish.Matte,
    stringGauge = StringGauge.Medium,
    numberOfFrets = 24,
    leftHanded = true
  )

  val order = GuitarOrder(
    customerName = "Aaron",
    model = stratModel,
    customSpecs = customSpecs
  )

  val factory = GuitarFactory()

  val guitarInventory = Inventory[String, Guitar]()

  val customGuitar = factory.create(order)

  guitarInventory.add(customGuitar)

  println("Guitar Created")
  println(s"ID: ${customGuitar.id}")
  println(s"Owner: ${customGuitar.owner}")
  println(s"Model: ${customGuitar.modelName}")
  println(s"Final Price: $$${customGuitar.finalPrice}")
  println(s"Specs: ${customGuitar.specs}")
  println(s"Total guitars in inventory: ${guitarInventory.count}")

  guitarInventory.all.foreach(guitar =>
    println(s"${guitar.id} - ${guitar.modelName} - Owner: ${guitar.owner}")
  )