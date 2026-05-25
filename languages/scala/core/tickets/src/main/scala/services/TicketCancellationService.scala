package services

import domain.{Ticket, TicketStatus}
import state.TicketState

class TicketCancellationService(
                                 state: TicketState
                               ):

  def cancelTicket(ticketId: Long): Either[String, Ticket] =
    state.synchronized {
      state.tickets.get(ticketId) match
        case None =>
          Left("Ticket not found.")

        case Some(ticket) if ticket.status == TicketStatus.Cancelled =>
          Left("Ticket is already cancelled.")

        case Some(ticket) =>
          val cancelledTicket =
            ticket.copy(status = TicketStatus.Cancelled)

          state.tickets.update(ticketId, cancelledTicket)

          Right(cancelledTicket)
    }
