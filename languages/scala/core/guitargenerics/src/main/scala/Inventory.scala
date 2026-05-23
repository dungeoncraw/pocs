import scala.collection.mutable

trait Identifiable[ID]:
  def id: ID


class Inventory[ID, Item <: Identifiable[ID]]:
  private val items: mutable.Map[ID, Item] = mutable.Map.empty

  def add(item: Item): Unit =
    items.addOne(item.id -> item)

  def remove(id: ID): Option[Item] =
    items.remove(id)

  def find(id: ID): Option[Item] =
    items.get(id)

  def all: List[Item] =
    items.values.toList

  def count: Int =
    items.size

  def exists(id: ID): Boolean =
    items.contains(id)