package state

import domain.{Seat, Show, ShowDate, Ticket, Venue, Zone}

import scala.collection.mutable

class TicketState(
                   initialShows: Vector[Show],
                   initialVenues: Vector[Venue],
                   initialZones: Vector[Zone],
                   initialSeats: Vector[Seat],
                   initialShowDates: Vector[ShowDate]
                 ):

  val shows: mutable.Map[Long, Show] =
    mutable.Map.from(initialShows.map(show => show.id -> show))

  val venues: mutable.Map[Long, Venue] =
    mutable.Map.from(initialVenues.map(venue => venue.id -> venue))

  val zones: mutable.Map[Long, Zone] =
    mutable.Map.from(initialZones.map(zone => zone.id -> zone))

  val seats: mutable.Map[Long, Seat] =
    mutable.Map.from(initialSeats.map(seat => seat.id -> seat))

  val showDates: mutable.Map[Long, ShowDate] =
    mutable.Map.from(initialShowDates.map(showDate => showDate.id -> showDate))

  val tickets: mutable.Map[Long, Ticket] =
    mutable.Map.empty

  private var nextTicketIdValue: Long = 1L

  def nextTicketId(): Long =
    val id = nextTicketIdValue
    nextTicketIdValue += 1
    id
