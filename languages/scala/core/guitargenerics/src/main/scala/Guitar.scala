case class Guitar(
                   id: String,
                   owner: String,
                   modelName: String,
                   specs: GuitarSpecs,
                   finalPrice: BigDecimal
                 ) extends Identifiable[String]