package models

import java.time.LocalDateTime

final case class Meeting(
                    id: String,
                    title: String,
                    participants: List[Person],
                    start: LocalDateTime,
                    end: LocalDateTime
                  )