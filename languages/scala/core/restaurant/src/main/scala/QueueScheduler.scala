import scala.collection.mutable

final class QueueScheduler(
                            stations: List[KitchenStation]
                          ):

  private val stationsById: Map[String, KitchenStation] =
    stations.map(station => station.id -> station).toMap

  private given slotOrdering: Ordering[StationSlot] =
    Ordering
      .by[StationSlot, Int](_.availableAtMinute)
      .reverse

  private def createStationHeaps(
                                  currentMinute: Int
                                ): Map[String, mutable.PriorityQueue[StationSlot]] =
    stations.map { station =>
      val heap = mutable.PriorityQueue.empty[StationSlot]

      for slotNumber <- 1 to station.capacity do
        heap.enqueue(
          StationSlot(
            id = s"${station.id}-slot-$slotNumber",
            stationId = station.id,
            availableAtMinute = currentMinute
          )
        )

      station.id -> heap
    }.toMap

  def scheduleOrders(
                      orders: List[Order],
                      currentMinute: Int = 0
                    ): List[ScheduledOrder] =

    val stationHeaps = createStationHeaps(currentMinute)

    val scheduledOrders =
      orders.map { order =>
        val scheduledDishes =
          order.items.flatMap { orderItem =>
            scheduleOrderItem(
              order = order,
              orderItem = orderItem,
              stationHeaps = stationHeaps,
              currentMinute = currentMinute
            )
          }

        val readyAt =
          if scheduledDishes.isEmpty then order.arrivalMinute
          else scheduledDishes.map(_.finishMinute).max

        ScheduledOrder(
          orderId = order.id,
          customerName = order.customerName,
          dishes = scheduledDishes,
          readyAtMinute = readyAt
        )
      }

    scheduledOrders

  private def scheduleOrderItem(
                                 order: Order,
                                 orderItem: OrderItem,
                                 stationHeaps: Map[String, mutable.PriorityQueue[StationSlot]],
                                 currentMinute: Int
                               ): List[Dish] =

    if orderItem.status == DishStatus.Cancelled then
      List.empty
    else
      val menuItem = orderItem.menuItem

      val station =
        stationsById.getOrElse(
          menuItem.stationId,
          throw new IllegalArgumentException(
            s"No kitchen station found for station id '${menuItem.stationId}'"
          )
        )

      val heap =
        stationHeaps.getOrElse(
          station.id,
          throw new IllegalArgumentException(
            s"No heap found for station id '${station.id}'"
          )
        )

      val quantity =
        math.max(orderItem.quantity, 1)

      (1 to quantity).map { itemNumber =>
        val earliestSlot = heap.dequeue()

        val startMinute =
          List(
            currentMinute,
            order.arrivalMinute,
            earliestSlot.availableAtMinute
          ).max

        val finishMinute =
          startMinute + menuItem.preparationMinutes

        val updatedSlot =
          earliestSlot.copy(
            availableAtMinute = finishMinute
          )

        heap.enqueue(updatedSlot)

        Dish(
          orderId = order.id,
          orderItemId =
            if quantity == 1 then orderItem.id
            else s"${orderItem.id}-$itemNumber",
          dishName = menuItem.name,
          stationId = station.id,
          stationName = station.name,
          slotId = earliestSlot.id,
          quantity = 1,
          preparationMinutes = menuItem.preparationMinutes,
          startMinute = startMinute,
          finishMinute = finishMinute
        )
      }.toList
