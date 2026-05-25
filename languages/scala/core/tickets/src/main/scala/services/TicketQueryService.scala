package services

import domain.{Ticket, TicketStatus}
import state.TicketState

class TicketQueryService(state: TicketState):

  def isActive(ticket: Ticket): Boolean =
    ticket.status == TicketStatus.Reserved ||
      ticket.status == TicketStatus.Paid

  def activeTicketsForShowDate(showDateId: Long): Vector[Ticket] =
    state.tickets.values
      .filter(ticket =>
        ticket.showDateId == showDateId &&
          isActive(ticket)
      )
      .toVector

  def activeTicketsForShowDateAndZone(
                                       showDateId: Long,
                                       zoneId: Long
                                     ): Vector[Ticket] =
    state.tickets.values
      .filter(ticket =>
        ticket.showDateId == showDateId &&
          ticket.zoneId == zoneId &&
          isActive(ticket)
      )
      .toVector

  def activeTicketExistsForSeat(
                                 showDateId: Long,
                                 seatId: Long
                               ): Boolean =
    state.tickets.values.exists(ticket =>
      ticket.showDateId == showDateId &&
        ticket.seatId == seatId &&
        isActive(ticket)
    )