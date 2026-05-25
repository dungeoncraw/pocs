package domain

case class BuyTicketRequest(
                             showDateId: Long,
                             zoneId: Long,
                             seatId: Long,
                             buyerId: Long,
                             payNow: Boolean
                           )