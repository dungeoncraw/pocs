

final case class Order(
                        id: String,
                        customerName: String,
                        items: List[OrderItem],
                        arrivalMinute: Int = 0
                      )
