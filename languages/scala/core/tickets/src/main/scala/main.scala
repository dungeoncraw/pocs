import domain.{BuyTicketRequest, Seat, Show, ShowDate, ShowStatus, Venue, Zone}
import services.{AvailabilityService, CapacityService, CatalogService, PricingService, PurchaseRules, TicketCancellationService, TicketPurchaseService, TicketQueryService}
import state.TicketState

import java.time.LocalDateTime

@main
def main(): Unit = {
  val shows =
    Vector(
      Show(
        id = 1,
        name = "Rock Night",
        description = "Live rock concert.",
        status = ShowStatus.Active
      )
    )

  val venues =
    Vector(
      Venue(
        id = 1,
        name = "Main Arena",
        location = "São Paulo",
        maxCapacity = 5
      )
    )

  val zones =
    Vector(
      Zone(
        id = 1,
        venueId = 1,
        name = "VIP",
        capacity = 2,
        priceModifier = BigDecimal(100)
      ),
      Zone(
        id = 2,
        venueId = 1,
        name = "Regular",
        capacity = 3,
        priceModifier = BigDecimal(0)
      )
    )

  val seats =
    Vector(
      Seat(id = 1, zoneId = 1, seatNumber = "VIP-A01"),
      Seat(id = 2, zoneId = 1, seatNumber = "VIP-A02"),
      Seat(id = 3, zoneId = 2, seatNumber = "REG-B01"),
      Seat(id = 4, zoneId = 2, seatNumber = "REG-B02"),
      Seat(id = 5, zoneId = 2, seatNumber = "REG-B03")
    )

  val showDates =
    Vector(
      ShowDate(
        id = 1,
        showId = 1,
        venueId = 1,
        date = LocalDateTime.of(2026, 8, 15, 20, 0),
        basePrice = BigDecimal(200)
      ),
      ShowDate(
        id = 2,
        showId = 1,
        venueId = 1,
        date = LocalDateTime.of(2026, 8, 16, 20, 0),
        basePrice = BigDecimal(200)
      )
    )

  val state =
    TicketState(
      initialShows = shows,
      initialVenues = venues,
      initialZones = zones,
      initialSeats = seats,
      initialShowDates = showDates
    )

  val catalogService =
    CatalogService(state)

  val ticketQueryService =
    TicketQueryService(state)

  val capacityService =
    CapacityService(
      catalogService = catalogService,
      ticketQueryService = ticketQueryService
    )

  val availabilityService =
    AvailabilityService(
      catalogService = catalogService,
      ticketQueryService = ticketQueryService
    )

  val pricingService =
    PricingService()

  val purchaseRules =
    PurchaseRules(
      capacityService = capacityService,
      ticketQueryService = ticketQueryService
    )

  val ticketPurchaseService =
    TicketPurchaseService(
      state = state,
      catalogService = catalogService,
      purchaseRules = purchaseRules,
      pricingService = pricingService
    )

  val cancellationService =
    TicketCancellationService(state)

  println(f"Available VIP seats before purchase ${availabilityService.listAvailableSeats(showDateId = 1, zoneId = 1)}")

  val ticket1 =
    ticketPurchaseService.buyTicket(
      BuyTicketRequest(
        showDateId = 1,
        zoneId = 1,
        seatId = 1,
        buyerId = 10,
        payNow = true
      )
    )

  println(f"Ticket 1: ${ticket1}")

  println(f"Available VIP seats after purchase ${availabilityService.listAvailableSeats(showDateId = 1, zoneId = 1)}")
  println(f"Another buyer tries to buy the same seat ${availabilityService.listAvailableSeats(showDateId = 1, zoneId = 1)}")
  val duplicatePurchase =
    ticketPurchaseService.buyTicket(
      BuyTicketRequest(
        showDateId = 1,
        zoneId = 1,
        seatId = 1,
        buyerId = 20,
        payNow = true
      )
    )

  println(f"Duplicate purchase: ${duplicatePurchase}")

  val ticket2 =
    ticketPurchaseService.buyTicket(
      BuyTicketRequest(
        showDateId = 1,
        zoneId = 1,
        seatId = 2,
        buyerId = 20,
        payNow = true
      )
    )

  println(f"Ticket 2: ${ticket2}")

  println(f"Available VIP seats: ${availabilityService.listAvailableSeats(showDateId = 1, zoneId = 1)}")

  println(f"Same seat can be bought for another date: ${availabilityService.listAvailableSeats(showDateId = 2, zoneId = 1)}")
  val sameSeatDifferentDate =
    ticketPurchaseService.buyTicket(
      BuyTicketRequest(
        showDateId = 2,
        zoneId = 1,
        seatId = 1,
        buyerId = 30,
        payNow = true
      )
    )

  println(f"Same seat different date: ${sameSeatDifferentDate}")
}

