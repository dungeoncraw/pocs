package domain

import java.time.LocalDateTime

case class ShowDate(
                     id: Long,
                     showId: Long,
                     venueId: Long,
                     date: LocalDateTime,
                     basePrice: BigDecimal
                   )