final case class OrderItem(
                            id: String,
                            menuItem: MenuItem,
                            quantity: Int = 1,
                            status: DishStatus = DishStatus.Queued
                          )