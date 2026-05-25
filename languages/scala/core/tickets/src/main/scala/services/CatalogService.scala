package services

import domain.{Seat, Show, ShowDate, Venue, Zone}
import state.TicketState

class CatalogService(state: TicketState):

  def findShow(id: Long): Either[String, Show] =
    state.shows.get(id).toRight(s"Show $id not found.")

  def findVenue(id: Long): Either[String, Venue] =
    state.venues.get(id).toRight(s"Venue $id not found.")

  def findZone(id: Long): Either[String, Zone] =
    state.zones.get(id).toRight(s"Zone $id not found.")

  def findSeat(id: Long): Either[String, Seat] =
    state.seats.get(id).toRight(s"Seat $id not found.")

  def findShowDate(id: Long): Either[String, ShowDate] =
    state.showDates.get(id).toRight(s"Show date $id not found.")

  def listShows(): Vector[Show] =
    state.shows.values.toVector

  def listShowDates(showId: Long): Vector[ShowDate] =
    state.showDates.values
      .filter(_.showId == showId)
      .toVector

  def listZonesForVenue(venueId: Long): Vector[Zone] =
    state.zones.values
      .filter(_.venueId == venueId)
      .toVector

  def listSeatsForZone(zoneId: Long): Vector[Seat] =
    state.seats.values
      .filter(_.zoneId == zoneId)
      .toVector
