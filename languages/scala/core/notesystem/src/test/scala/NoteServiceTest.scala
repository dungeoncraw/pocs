package notesystem

import os.*

class NoteServiceTest extends munit.FunSuite {
  val testStorage = os.pwd / "test-notes.json"

  override def beforeEach(context: BeforeEach): Unit = {
    if (os.exists(testStorage)) os.remove(testStorage)
  }

  test("addNote should add a note and save it") {
    val service = NoteService(testStorage)
    val note = service.addNote("Test Title", "Test Content")
    
    assertEquals(service.getNotes.size, 1)
    assertEquals(service.getNotes.head.title, "Test Title")
    assert(os.exists(testStorage))
  }

  test("editNote should update an existing note") {
    val service = NoteService(testStorage)
    val note = service.addNote("Old Title", "Old Content")
    val updated = service.editNote(note.id, "New Title", "New Content")
    
    assertEquals(updated.get.title, "New Title")
    assertEquals(service.getNotes.head.title, "New Title")
  }

  test("deleteNote should remove the note") {
    val service = NoteService(testStorage)
    val note = service.addNote("To Delete", "Content")
    assertEquals(service.getNotes.size, 1)
    
    val deleted = service.deleteNote(note.id)
    assert(deleted)
    assertEquals(service.getNotes.size, 0)
  }

  test("sync should reload notes from disk") {
    val service1 = NoteService(testStorage)
    service1.addNote("Shared Note", "Content")
    
    val service2 = NoteService(testStorage)
    assertEquals(service2.getNotes.size, 1)
    assertEquals(service2.getNotes.head.title, "Shared Note")
  }
}
