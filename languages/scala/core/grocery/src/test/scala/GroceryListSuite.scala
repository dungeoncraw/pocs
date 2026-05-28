class GroceryListSuite extends munit.FunSuite:

  test("addItem should add grocery items in order") {
    val list = GroceryList()

    val milk = list.addItem("Milk")
    val bread = list.addItem("Bread")

    assertEquals(
      list.listAll(),
      List(
        GroceryItem(milk.id, "Milk", done = false),
        GroceryItem(bread.id, "Bread", done = false)
      )
    )
  }

  test("removeItem should remove an existing item") {
    val list = GroceryList()

    val milk = list.addItem("Milk")
    val bread = list.addItem("Bread")

    val result = list.removeItem(milk.id)

    assertEquals(result, Right(milk))
    assertEquals(list.listAll(), List(bread))
  }

  test("removeItem should fail when item does not exist") {
    val list = GroceryList()

    val result = list.removeItem(999)

    assertEquals(result, Left("Item with id 999 was not found."))
  }

  test("markAsDone should mark an item as done") {
    val list = GroceryList()

    val milk = list.addItem("Milk")

    val result = list.markAsDone(milk.id)

    assertEquals(result, Right(milk.copy(done = true)))
    assertEquals(list.listAll(), List(milk.copy(done = true)))
  }

  test("markAsDone should fail when item does not exist") {
    val list = GroceryList()

    val result = list.markAsDone(999)

    assertEquals(result, Left("Item with id 999 was not found."))
  }

  test("markAsDone should fail when item is already done") {
    val list = GroceryList()

    val milk = list.addItem("Milk")

    list.markAsDone(milk.id)
    val result = list.markAsDone(milk.id)

    assertEquals(result, Left(s"Item with id ${milk.id} is already done."))
  }

  test("undo should undo an add action") {
    val list = GroceryList()

    val milk = list.addItem("Milk")

    val result = list.undo()

    assertEquals(result, Right(()))
    assertEquals(list.listAll(), List.empty)
  }

  test("undo should undo a remove action") {
    val list = GroceryList()

    val milk = list.addItem("Milk")
    val bread = list.addItem("Bread")

    list.removeItem(milk.id)
    list.undo()

    assertEquals(
      list.listAll().toSet,
      Set(milk, bread)
    )
  }

  test("undo should undo a markAsDone action") {
    val list = GroceryList()

    val milk = list.addItem("Milk")

    list.markAsDone(milk.id)
    list.undo()

    assertEquals(list.listAll(), List(milk))
  }

  test("undo should fail when there is nothing to undo") {
    val list = GroceryList()

    val result = list.undo()

    assertEquals(result, Left("Nothing to undo."))
  }

  test("redo should redo an undone add action") {
    val list = GroceryList()

    val milk = list.addItem("Milk")

    list.undo()
    val result = list.redo()

    assertEquals(result, Right(()))
    assertEquals(list.listAll(), List(milk))
  }

  test("redo should redo an undone remove action") {
    val list = GroceryList()

    val milk = list.addItem("Milk")
    val bread = list.addItem("Bread")

    list.removeItem(milk.id)

    assertEquals(list.listAll(), List(bread))

    list.undo()

    assertEquals(list.listAll().toSet, Set(milk, bread))

    val result = list.redo()

    assertEquals(result, Right(()))
    assertEquals(list.listAll(), List(bread))
  }

  test("redo should redo an undone markAsDone action") {
    val list = GroceryList()

    val milk = list.addItem("Milk")

    list.markAsDone(milk.id)
    list.undo()

    assertEquals(list.listAll(), List(milk))

    val result = list.redo()

    assertEquals(result, Right(()))
    assertEquals(list.listAll(), List(milk.copy(done = true)))
  }

  test("redo should fail when there is nothing to redo") {
    val list = GroceryList()

    val result = list.redo()

    assertEquals(result, Left("Nothing to redo."))
  }

  test("new action after undo should clear redo history") {
    val list = GroceryList()

    val milk = list.addItem("Milk")
    val bread = list.addItem("Bread")

    list.undo()

    assertEquals(list.listAll(), List(milk))

    val eggs = list.addItem("Eggs")

    assertEquals(
      list.listAll(),
      List(milk, eggs)
    )

    val redoResult = list.redo()

    assertEquals(redoResult, Left("Nothing to redo."))
  }

  test("multiple undo and redo actions should work in order") {
    val list = GroceryList()

    val milk = list.addItem("Milk")
    val bread = list.addItem("Bread")
    val eggs = list.addItem("Eggs")

    list.markAsDone(bread.id)

    assertEquals(
      list.listAll(),
      List(
        milk,
        bread.copy(done = true),
        eggs
      )
    )

    list.undo()

    assertEquals(
      list.listAll(),
      List(
        milk,
        bread,
        eggs
      )
    )

    list.undo()

    assertEquals(
      list.listAll(),
      List(
        milk,
        bread
      )
    )

    list.redo()

    assertEquals(
      list.listAll(),
      List(
        milk,
        bread,
        eggs
      )
    )

    list.redo()

    assertEquals(
      list.listAll(),
      List(
        milk,
        bread.copy(done = true),
        eggs
      )
    )
  }