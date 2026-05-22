class GuitarService(inventory: Inventory):
  private var nextSerial = 1

  def build(spec: GuitarSpec): Either[String, Guitar] =
    val requiredParts: List[GuitarPart] = List(
      spec.bodyWood,
      spec.neckWood,
      spec.pickup,
      spec.bridge,
      spec.finish,
      spec.os
    )

    val missingParts = requiredParts.filterNot(inventory.has)

    if missingParts.nonEmpty then
      Left(s"Cannot build guitar. Missing parts: ${missingParts.map(_.label).mkString(", ")}")
    else
      requiredParts.foreach(inventory.use)

      val serial = f"GF-$nextSerial%04d"
      nextSerial += 1

      Right(Guitar(serial, spec))
