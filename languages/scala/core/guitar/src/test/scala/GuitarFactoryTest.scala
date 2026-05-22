import munit.FunSuite

class GuitarFactoryTest extends FunSuite {

  test("Inventory should start with correct enum-based quantities") {
    val inventory = new Inventory()

    assertEquals(inventory.quantity(BodyWood.AlderBody), 5)
    assertEquals(inventory.quantity(BodyWood.MahoganyBody), 3)

    assertEquals(inventory.quantity(NeckWood.MapleNeck), 6)
    assertEquals(inventory.quantity(NeckWood.RosewoodNeck), 4)

    assertEquals(inventory.quantity(Pickup.SingleCoil), 10)
    assertEquals(inventory.quantity(Pickup.Humbucker), 8)

    assertEquals(inventory.quantity(Bridge.Fixed), 5)
    assertEquals(inventory.quantity(Bridge.Tremolo), 4)

    assertEquals(inventory.quantity(Finish.Black), 6)
    assertEquals(inventory.quantity(Finish.Sunburst), 5)

    assertEquals(inventory.quantity(GuitarOS.PassiveAnalog), 10)
    assertEquals(inventory.quantity(GuitarOS.SmartToneOS), 3)
  }

  test("Inventory should know when a part is available") {
    val inventory = new Inventory()

    assertEquals(inventory.has(BodyWood.AlderBody), true)
    assertEquals(inventory.has(Pickup.SingleCoil), true)
    assertEquals(inventory.has(GuitarOS.SmartToneOS), true)
  }

  test("Inventory should use one part and reduce quantity by one") {
    val inventory = new Inventory()

    val used = inventory.use(BodyWood.AlderBody)

    assertEquals(used, true)
    assertEquals(inventory.quantity(BodyWood.AlderBody), 4)
  }

  test("Inventory should return false when using a part with zero quantity") {
    val inventory = new Inventory()

    inventory.use(BodyWood.AlderBody)
    inventory.use(BodyWood.AlderBody)
    inventory.use(BodyWood.AlderBody)
    inventory.use(BodyWood.AlderBody)
    inventory.use(BodyWood.AlderBody)

    assertEquals(inventory.quantity(BodyWood.AlderBody), 0)
    assertEquals(inventory.has(BodyWood.AlderBody), false)

    val result = inventory.use(BodyWood.AlderBody)

    assertEquals(result, false)
    assertEquals(inventory.quantity(BodyWood.AlderBody), 0)
  }

  test("GuitarSpec should use enum values") {
    val spec = GuitarSpec(
      model = GuitarModel.CustomStrat,
      bodyWood = BodyWood.AlderBody,
      neckWood = NeckWood.MapleNeck,
      pickup = Pickup.SingleCoil,
      bridge = Bridge.Tremolo,
      finish = Finish.Sunburst,
      os = GuitarOS.SmartToneOS
    )

    assertEquals(spec.model.label, "Custom Strat")
    assertEquals(spec.bodyWood.label, "Alder Body")
    assertEquals(spec.neckWood.label, "Maple Neck")
    assertEquals(spec.pickup.label, "Single Coil Pickup")
    assertEquals(spec.bridge.label, "Tremolo Bridge")
    assertEquals(spec.finish.label, "Sunburst Finish")
    assertEquals(spec.os.label, "Smart Tone OS")
  }

  test("Factory should build a guitar when inventory has all required parts") {
    val inventory = new Inventory()
    val factory = new GuitarService(inventory)

    val spec = GuitarSpec(
      model = GuitarModel.CustomStrat,
      bodyWood = BodyWood.AlderBody,
      neckWood = NeckWood.MapleNeck,
      pickup = Pickup.SingleCoil,
      bridge = Bridge.Tremolo,
      finish = Finish.Sunburst,
      os = GuitarOS.SmartToneOS
    )

    val result = factory.build(spec)

    assert(result.isRight)

    assertEquals(inventory.quantity(BodyWood.AlderBody), 4)
    assertEquals(inventory.quantity(NeckWood.MapleNeck), 5)
    assertEquals(inventory.quantity(Pickup.SingleCoil), 9)
    assertEquals(inventory.quantity(Bridge.Tremolo), 3)
    assertEquals(inventory.quantity(Finish.Sunburst), 4)
    assertEquals(inventory.quantity(GuitarOS.SmartToneOS), 2)
  }

  test("Factory should not build a guitar when one required part is missing") {
    val inventory = new Inventory()
    val factory = new GuitarService(inventory)

    inventory.use(GuitarOS.SmartToneOS)
    inventory.use(GuitarOS.SmartToneOS)
    inventory.use(GuitarOS.SmartToneOS)

    assertEquals(inventory.quantity(GuitarOS.SmartToneOS), 0)

    val spec = GuitarSpec(
      model = GuitarModel.CustomStrat,
      bodyWood = BodyWood.AlderBody,
      neckWood = NeckWood.MapleNeck,
      pickup = Pickup.SingleCoil,
      bridge = Bridge.Tremolo,
      finish = Finish.Sunburst,
      os = GuitarOS.SmartToneOS
    )

    val result = factory.build(spec)

    assert(result.isLeft)

    assertEquals(inventory.quantity(GuitarOS.SmartToneOS), 0)
  }

  test("Guitar description should use readable enum labels") {
    val spec = GuitarSpec(
      model = GuitarModel.CustomStrat,
      bodyWood = BodyWood.AlderBody,
      neckWood = NeckWood.MapleNeck,
      pickup = Pickup.SingleCoil,
      bridge = Bridge.Tremolo,
      finish = Finish.Sunburst,
      os = GuitarOS.SmartToneOS
    )

    val guitar = Guitar("g-something",spec)

    assert(guitar.description.contains("Custom Strat"))
    assert(guitar.description.contains("Alder Body"))
    assert(guitar.description.contains("Maple Neck"))
    assert(guitar.description.contains("Single Coil Pickup"))
    assert(guitar.description.contains("Tremolo Bridge"))
    assert(guitar.description.contains("Sunburst Finish"))
    assert(guitar.description.contains("Smart Tone OS"))
  }
}