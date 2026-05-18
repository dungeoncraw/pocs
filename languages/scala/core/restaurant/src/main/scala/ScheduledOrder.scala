final case class ScheduledOrder(
                                 orderId: String,
                                 customerName: String,
                                 dishes: List[Dish],
                                 readyAtMinute: Int
                               )