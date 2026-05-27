
@main
def main(): Unit = {
  val groceryList = GroceryList()

  val milk = groceryList.addItem("Milk")
  val bread = groceryList.addItem("Bread")
  val eggs = groceryList.addItem("Eggs")

  println("After adding items:")
  groceryList.listAll().foreach(println)

  groceryList.markAsDone(bread.id)

  println("\nAfter marking Bread as done:")
  groceryList.listAll().foreach(println)

  groceryList.removeItem(milk.id)

  println("\nAfter removing Milk:")
  groceryList.listAll().foreach(println)

  groceryList.undo()

  println("\nAfter undo:")
  groceryList.listAll().foreach(println)

  groceryList.undo()

  println("\nAfter another undo:")
  groceryList.listAll().foreach(println)

  groceryList.redo()

  println("\nAfter redo:")
  groceryList.listAll().foreach(println)
}

