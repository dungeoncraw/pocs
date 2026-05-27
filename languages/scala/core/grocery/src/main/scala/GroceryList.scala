import scala.collection.mutable

final case class GroceryItem(
                              id: Int,
                              name: String,
                              done: Boolean = false
                            )

enum Action:
  case Added(item: GroceryItem)
  case Removed(item: GroceryItem)
  case MarkedDone(before: GroceryItem, after: GroceryItem)

class GroceryList:

  private val items = mutable.LinkedHashMap.empty[Int, GroceryItem]

  private val history = mutable.ArrayBuffer.empty[Action]

  private var cursor: Int = 0
  private var nextId: Int = 1

  def addItem(name: String): GroceryItem =
    val item = GroceryItem(nextId, name)
    nextId += 1

    doAction(Action.Added(item))
    item

  def removeItem(id: Int): Either[String, GroceryItem] =
    items.get(id) match
      case None =>
        Left(s"Item with id $id was not found.")

      case Some(item) =>
        doAction(Action.Removed(item))
        Right(item)

  def markAsDone(id: Int): Either[String, GroceryItem] =
    items.get(id) match
      case None =>
        Left(s"Item with id $id was not found.")

      case Some(item) if item.done =>
        Left(s"Item with id $id is already done.")

      case Some(before) =>
        val after = before.copy(done = true)
        doAction(Action.MarkedDone(before, after))
        Right(after)

  def undo(): Either[String, Unit] =
    if cursor == 0 then
      Left("Nothing to undo.")
    else
      cursor -= 1
      val action = history(cursor)
      reverse(action)
      Right(())

  def redo(): Either[String, Unit] =
    if cursor == history.length then
      Left("Nothing to redo.")
    else
      val action = history(cursor)
      matchAction(action)
      cursor += 1
      Right(())

  def listAll(): List[GroceryItem] =
    items.values.toList

  private def doAction(action: Action): Unit =
    if cursor < history.length then
      history.remove(cursor, history.length - cursor)

    matchAction(action)
    history.append(action)
    cursor += 1

  private def matchAction(action: Action): Unit =
    action match
      case Action.Added(item) =>
        items.addOne(item.id -> item)

      case Action.Removed(item) =>
        items.remove(item.id)

      case Action.MarkedDone(_, after) =>
        items.update(after.id, after)

  private def reverse(action: Action): Unit =
    action match
      case Action.Added(item) =>
        items.remove(item.id)

      case Action.Removed(item) =>
        items.addOne(item.id -> item)

      case Action.MarkedDone(before, _) =>
        items.update(before.id, before)