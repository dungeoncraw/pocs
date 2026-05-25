package domain

import java.time.LocalDateTime

case class Ticket(
                   id: Long,
                   showDateId: Long,
                   zoneId: Long,
                   seatId: Long,
                   buyerId: Long,
                   status: TicketStatus,
                   price: BigDecimal,
                   createdAt: LocalDateTime
                 )