use challenge_06_non_exhaustive_match::*;

#[test]
fn labels_for_happy_path() {
    assert_eq!(label(OrderStatus::Pending), "Waiting for payment");
    assert_eq!(label(OrderStatus::Paid), "Payment received");
    assert_eq!(label(OrderStatus::Shipped), "On its way");
    assert_eq!(label(OrderStatus::Delivered), "Delivered");
}

#[test]
fn label_for_cancelled() {
    assert_eq!(label(OrderStatus::Cancelled), "Order cancelled");
}

#[test]
fn cancel_and_next_rules() {
    assert!(can_cancel(OrderStatus::Paid));
    assert!(!can_cancel(OrderStatus::Shipped));
    assert_eq!(next(OrderStatus::Paid), Some(OrderStatus::Shipped));
    assert_eq!(next(OrderStatus::Cancelled), None);
}
