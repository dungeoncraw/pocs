// simple singleton enum, so can have only one instance of each case
enum OrderStatus:
  case Pending
  case Paid
  case Shipped
  case Cancelled

  def describeStatus(): String =
    this match
      case OrderStatus.Pending => "Order is pending"
      case OrderStatus.Paid => "Order is paid"
      case OrderStatus.Shipped => "Order was shipped"
      case OrderStatus.Cancelled => "Order was cancelled"


// enum with parameters, so each case can have different data associated with it
enum OrderError:
  case OrderNotFound(orderId: String)
  case PaymentRejected(reason: String)
  case InvalidQuantity(quantity: Int)

  def describeError(): String =
    this match
      case OrderError.OrderNotFound(orderId) => s"Order not found: $orderId"
      case OrderError.PaymentRejected(reason) => s"Payment rejected: $reason"
      case OrderError.InvalidQuantity(quantity) => s"Invalid quantity: $quantity"

@main
def main(): Unit =
  val statusPaid: OrderStatus = OrderStatus.Paid
  println(statusPaid.describeStatus())
  val statusPaid2 = OrderStatus.Paid
  // expect true as it is the same instance
  println(statusPaid2 == statusPaid)

  val errorNotFound: OrderError =
    OrderError.OrderNotFound("order-123")

  println(errorNotFound.describeError())

  val errorNotFound2: OrderError =
    OrderError.OrderNotFound("order-321")
  // expect false as the carrying data is different
  println(errorNotFound2 == errorNotFound)