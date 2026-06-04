
import notesystem.*
import os.*
import scala.io.StdIn

@main
def main(): Unit = {
  val storagePath = os.pwd / "notes.json"
  val service = NoteService(storagePath)

  var continue = true
  while (continue) {
    println("\n--- Note Taking System ---")
    println("1. Add Note")
    println("2. List Notes")
    println("3. Edit Note")
    println("4. Delete Note")
    println("5. Sync")
    println("6. Exit")
    print("Choose an option: ")

    StdIn.readLine() match {
      case "1" =>
        print("Enter title: ")
        val title = StdIn.readLine()
        print("Enter content: ")
        val content = StdIn.readLine()
        service.addNote(title, content)
        println("Note added successfully.")

      case "2" =>
        println("\nYour Notes:")
        service.getNotes.foreach { note =>
          println(s"ID: ${note.id} | Title: ${note.title} | Last Updated: ${note.updatedAt}")
          println(s"Content: ${note.content}")
          println("-" * 20)
        }

      case "3" =>
        print("Enter Note ID to edit: ")
        val id = StdIn.readLine()
        print("Enter new title: ")
        val title = StdIn.readLine()
        print("Enter new content: ")
        val content = StdIn.readLine()
        service.editNote(id, title, content) match {
          case Some(_) => println("Note updated.")
          case None => println("Note not found.")
        }

      case "4" =>
        print("Enter Note ID to delete: ")
        val id = StdIn.readLine()
        if (service.deleteNote(id)) println("Note deleted.")
        else println("Note not found.")

      case "5" =>
        service.sync()

      case "6" =>
        continue = false

      case _ =>
        println("Invalid option.")
    }
  }
}

