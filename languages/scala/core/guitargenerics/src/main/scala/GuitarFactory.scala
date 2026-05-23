
trait Factory[Input, Output]:
  def create(input: Input): Output

class GuitarFactory extends Factory[GuitarOrder, Guitar]:

  private var nextId: Int = 1

  override def create(order: GuitarOrder): Guitar =
    val generatedId =   String.format("GTR-%04d", nextId)
    nextId += 1

    val price = calculatePrice(order.model.basePrice, order.customSpecs)

    Guitar(
      id = generatedId,
      owner = order.customerName,
      modelName = order.model.name,
      specs = order.customSpecs,
      finalPrice = price
    )

  private def calculatePrice(
                              basePrice: BigDecimal,
                              specs: GuitarSpecs
                            ): BigDecimal =
    var price = basePrice

    if specs.leftHanded then price += 150
    if specs.pickups.contains(Pickup.Humbucker) then price += 200
    if specs.bridge == Bridge.FloydRose then price += 300
    if specs.finish == Finish.Gloss then price += 100
    if specs.numberOfFrets > 22 then price += 120

    specs.bodyWood match
      case Wood.Mahogany => price += 250
      case Wood.Maple    => price += 200
      case Wood.Alder    => price += 120
      case Wood.Basswood => price += 80
      case Wood.Spruce   => price += 150
      case Wood.Rosewood => price += 300

    price