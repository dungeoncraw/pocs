package services

import domain.{Seat, Zone}

class AvailabilityService(
                           catalogService: CatalogService,
                           ticketQueryService: TicketQueryService
                         ):

  def listAvailableSeats(
                          showDateId: Long,
                          zoneId: Long
                        ): Either[String, Vector[Seat]] =
    for
      showDate <- catalogService.findShowDate(showDateId)
      zone <- catalogService.findZone(zoneId)
      _ <- validateZoneBelongsToVenue(zone, showDate.venueId)
    yield
      val unavailableSeatIds =
        ticketQueryService
          .activeTicketsForShowDateAndZone(showDateId, zoneId)
          .map(_.seatId)
          .toSet

      catalogService
        .listSeatsForZone(zoneId)
        .filterNot(seat => unavailableSeatIds.contains(seat.id))

  private def validateZoneBelongsToVenue(
                                          zone: Zone,
                                          venueId: Long
                                        ): Either[String, Unit] =
    if zone.venueId == venueId then Right(())
    else Left("Selected zone does not belong to this venue.")

