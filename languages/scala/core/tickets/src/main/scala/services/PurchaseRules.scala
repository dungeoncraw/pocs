package services

import domain.{Seat, Show, ShowDate, ShowStatus, Venue, Zone}

class PurchaseRules(
                     capacityService: CapacityService,
                     ticketQueryService: TicketQueryService
                   ):

  def validatePurchase(
                        show: Show,
                        venue: Venue,
                        zone: Zone,
                        seat: Seat,
                        showDate: ShowDate
                      ): Either[String, Unit] =
    for
      _ <- validateShowIsActive(show)
      _ <- validateZoneBelongsToVenue(zone, venue.id)
      _ <- validateSeatBelongsToZone(seat, zone.id)
      _ <- capacityService.validateVenueHasCapacity(showDate.id, venue)
      _ <- capacityService.validateZoneHasCapacity(showDate.id, zone)
      _ <- validateSeatIsAvailable(showDate.id, seat.id)
    yield ()

  private def validateShowIsActive(show: Show): Either[String, Unit] =
    show.status match
      case ShowStatus.Active =>
        Right(())

      case ShowStatus.Cancelled =>
        Left("Show is cancelled.")

      case ShowStatus.SoldOut =>
        Left("Show is sold out.")

  private def validateZoneBelongsToVenue(
                                          zone: Zone,
                                          venueId: Long
                                        ): Either[String, Unit] =
    if zone.venueId == venueId then Right(())
    else Left("Selected zone does not belong to this venue.")

  private def validateSeatBelongsToZone(
                                         seat: Seat,
                                         zoneId: Long
                                       ): Either[String, Unit] =
    if seat.zoneId == zoneId then Right(())
    else Left("Selected seat does not belong to this zone.")

  private def validateSeatIsAvailable(
                                       showDateId: Long,
                                       seatId: Long
                                     ): Either[String, Unit] =
    val alreadyTaken =
      ticketQueryService.activeTicketExistsForSeat(showDateId, seatId)

    if alreadyTaken then Left("Seat is already sold for this show date.")
    else Right(())
