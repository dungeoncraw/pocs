package notesystem

import upickle.default.*
import os.*
import java.time.Instant
import java.util.UUID

class NoteService(storagePath: os.Path) {
  private var notes: Map[String, Note] = loadNotes()

  private def loadNotes(): Map[String, Note] = {
    if (os.exists(storagePath)) {
      val json = os.read(storagePath)
      if (json.trim.isEmpty) Map.empty
      else upickle.default.read[Map[String, Note]](json)
    } else {
      Map.empty
    }
  }

  def save(): Unit = {
    os.write.over(storagePath, upickle.default.write(notes, indent = 2), createFolders = true)
  }

  def addNote(title: String, content: String): Note = {
    val id = UUID.randomUUID().toString
    val note = Note(id, title, content)
    notes += (id -> note)
    save()
    note
  }

  def editNote(id: String, title: String, content: String): Option[Note] = {
    notes.get(id).map { oldNote =>
      val updatedNote = oldNote.copy(
        title = title,
        content = content,
        updatedAt = Instant.now().getEpochSecond
      )
      notes += (id -> updatedNote)
      save()
      updatedNote
    }
  }

  def deleteNote(id: String): Boolean = {
    if (notes.contains(id)) {
      notes -= id
      save()
      true
    } else {
      false
    }
  }

  def getNotes: List[Note] = notes.values.toList.sortBy(_.updatedAt)(using Ordering[Long].reverse)

  def sync(): Unit = {
    notes = loadNotes()
    println("Synced with local storage.")
  }
}
