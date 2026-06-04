package notesystem

import upickle.default.*
import java.time.Instant

case class Note(
    id: String,
    title: String,
    content: String,
    updatedAt: Long = Instant.now().getEpochSecond
) derives ReadWriter
