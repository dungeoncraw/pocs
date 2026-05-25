package services

import domain.{BuyTicketRequest, Ticket, TicketStatus}
import state.TicketState

import java.time.LocalDateTime

class TicketPurchaseService(
                             state: TicketState,
                             catalogService: CatalogService,
                             purchaseRules: PurchaseRules,
                             pricingService: PricingService
                           ):

  def buyTicket(request: BuyTicketRequest): Either[String, Ticket] =
    state.synchronized {
      for
        showDate <- catalogService.findShowDate(request.showDateId)
        show <- catalogService.findShow(showDate.showId)
        venue <- catalogService.findVenue(showDate.venueId)
        zone <- catalogService.findZone(request.zoneId)
        seat <- catalogService.findSeat(request.seatId)

        _ <- purchaseRules.validatePurchase(
          show = show,
          venue = venue,
          zone = zone,
          seat = seat,
          showDate = showDate
        )

      yield
        val status =
          if request.payNow then TicketStatus.Paid
          else TicketStatus.Reserved

        val ticket =
          Ticket(
            id = state.nextTicketId(),
            showDateId = showDate.id,
            zoneId = zone.id,
            seatId = seat.id,
            buyerId = request.buyerId,
            status = status,
            price = pricingService.calculatePrice(showDate, zone),
            createdAt = LocalDateTime.now()
          )

        state.tickets.addOne(ticket.id -> ticket)

        ticket
    }
