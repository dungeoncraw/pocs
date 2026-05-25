package domain

case class Zone(
                 id: Long,
                 venueId: Long,
                 name: String,
                 capacity: Int,
                 priceModifier: BigDecimal
               )