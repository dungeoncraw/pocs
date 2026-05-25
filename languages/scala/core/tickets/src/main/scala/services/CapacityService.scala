package services

import domain.{Venue, Zone}

class CapacityService(
                       catalogService: CatalogService,
                       ticketQueryService: TicketQueryService
                     ):

  def validateVenueZoneSetup(venueId: Long): Either[String, Unit] =
    catalogService.findVenue(venueId).flatMap { venue =>
      val totalZoneCapacity =
        catalogService
          .listZonesForVenue(venue.id)
          .map(_.capacity)
          .sum

      if totalZoneCapacity <= venue.maxCapacity then Right(())
      else
        Left(
          s"Venue '${venue.name}' max capacity is ${venue.maxCapacity}, " +
            s"but zones total $totalZoneCapacity."
        )
    }

  def validateVenueHasCapacity(
                                showDateId: Long,
                                venue: Venue
                              ): Either[String, Unit] =
    val soldOrReserved =
      ticketQueryService.activeTicketsForShowDate(showDateId).size

    if soldOrReserved < venue.maxCapacity then Right(())
    else Left("Venue maximum capacity reached.")

  def validateZoneHasCapacity(
                               showDateId: Long,
                               zone: Zone
                             ): Either[String, Unit] =
    val soldOrReserved =
      ticketQueryService
        .activeTicketsForShowDateAndZone(showDateId, zone.id)
        .size

    if soldOrReserved < zone.capacity then Right(())
    else Left(s"Zone '${zone.name}' is sold out.")
