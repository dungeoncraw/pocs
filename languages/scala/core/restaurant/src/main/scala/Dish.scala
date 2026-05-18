final case class Dish(
                                orderId: String,
                                orderItemId: String,
                                dishName: String,
                                stationId: String,
                                stationName: String,
                                slotId: String,
                                quantity: Int,
                                preparationMinutes: Int,
                                startMinute: Int,
                                finishMinute: Int
                              )