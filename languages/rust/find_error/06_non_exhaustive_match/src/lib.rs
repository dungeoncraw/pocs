#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum OrderStatus {
    Pending,
    Paid,
    Shipped,
    Delivered,
    Cancelled,
}

/// Human readable label for an order status.
pub fn label(status: OrderStatus) -> &'static str {
    match status {
        OrderStatus::Pending => "Waiting for payment",
        OrderStatus::Paid => "Payment received",
        OrderStatus::Shipped => "On its way",
        OrderStatus::Delivered => "Delivered",
    }
}

/// Whether the customer can still cancel the order.
pub fn can_cancel(status: OrderStatus) -> bool {
    matches!(status, OrderStatus::Pending | OrderStatus::Paid)
}

/// The next status in the happy path, if there is one.
pub fn next(status: OrderStatus) -> Option<OrderStatus> {
    match status {
        OrderStatus::Pending => Some(OrderStatus::Paid),
        OrderStatus::Paid => Some(OrderStatus::Shipped),
        OrderStatus::Shipped => Some(OrderStatus::Delivered),
        OrderStatus::Delivered | OrderStatus::Cancelled => None,
    }
}
