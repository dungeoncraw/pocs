@main
def main(): Unit =

  val grill =
    KitchenStation(
      id = "grill",
      name = "Grill Station",
      capacity = 2
    )

  val fryer =
    KitchenStation(
      id = "fryer",
      name = "Fryer Station",
      capacity = 1
    )

  val drinks =
    KitchenStation(
      id = "drinks",
      name = "Drinks Station",
      capacity = 2
    )

  val stations =
    List(grill, fryer, drinks)

  val burger =
    MenuItem(
      id = "burger",
      name = "Burger",
      stationId = "grill",
      preparationMinutes = 12
    )

  val steak =
    MenuItem(
      id = "steak",
      name = "Steak",
      stationId = "grill",
      preparationMinutes = 18
    )

  val fries =
    MenuItem(
      id = "fries",
      name = "Fries",
      stationId = "fryer",
      preparationMinutes = 6
    )

  val nuggets =
    MenuItem(
      id = "nuggets",
      name = "Nuggets",
      stationId = "fryer",
      preparationMinutes = 8
    )

  val coffee =
    MenuItem(
      id = "coffee",
      name = "Coffee",
      stationId = "drinks",
      preparationMinutes = 3
    )

  val soda =
    MenuItem(
      id = "soda",
      name = "Soda",
      stationId = "drinks",
      preparationMinutes = 1
    )

  val orders =
    List(
      Order(
        id = "order-1",
        customerName = "Jon",
        items = List(
          OrderItem(id = "order-1-item-1", menuItem = burger),
          OrderItem(id = "order-1-item-2", menuItem = fries),
          OrderItem(id = "order-1-item-3", menuItem = soda)
        )
      ),
      Order(
        id = "order-2",
        customerName = "Jacob",
        items = List(
          OrderItem(id = "order-2-item-1", menuItem = steak),
          OrderItem(id = "order-2-item-2", menuItem = coffee)
        )
      ),
      Order(
        id = "order-3",
        customerName = "Jingleheimer Smith",
        items = List(
          OrderItem(id = "order-3-item-1", menuItem = nuggets),
          OrderItem(id = "order-3-item-2", menuItem = burger)
        )
      )
    )

  val scheduler =
    QueueScheduler(stations)

  val scheduledOrders =
    scheduler.scheduleOrders(
      orders = orders)

  printSchedule(scheduledOrders)

private def printSchedule(
                           scheduledOrders: List[ScheduledOrder]
                         ): Unit =

  println()
  println("Restaurant Queue Schedule")

  scheduledOrders.foreach { order =>
    println()
    println(s"Order: ${order.orderId}")
    println(s"Customer: ${order.customerName}")
    println(s"Ready in: ${order.readyAtMinute} minutes")
    println("Dishes:")

    order.dishes.foreach { dish =>
      println(
        s"""|  - ${dish.dishName}
            |    Station: ${dish.stationName}
            |    Slot: ${dish.slotId}
            |    Starts at minute: ${dish.startMinute}
            |    Finishes at minute: ${dish.finishMinute}
            |    Ready in: ${dish.finishMinute} minutes
            |""".stripMargin
      )
    }
  }