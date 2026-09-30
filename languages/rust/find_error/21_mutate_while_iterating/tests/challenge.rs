use challenge_21_mutate_while_iterating::*;

fn sample() -> Inventory {
    let mut inv = Inventory::new();
    inv.add("bolts", 100);
    inv.add("nuts", 2);
    inv.add("washers", 0);
    inv.add("screws", 4);
    inv
}

#[test]
fn add_accumulates() {
    let mut inv = sample();
    inv.add("nuts", 3);
    assert_eq!(inv.quantity("nuts"), 5);
    assert_eq!(inv.quantity("missing"), 0);
}

#[test]
fn restock_refills_only_low_items() {
    let mut inv = sample();
    inv.restock(5, 50);
    assert_eq!(inv.quantity("bolts"), 100);
    assert_eq!(inv.quantity("nuts"), 50);
    assert_eq!(inv.quantity("washers"), 50);
    assert_eq!(inv.quantity("screws"), 50);
    assert_eq!(inv.len(), 4);
    assert!(inv.low_stock(5).is_empty());
}

#[test]
fn prune_removes_only_empty_items() {
    let mut inv = sample();
    inv.add("gaskets", 0);
    assert_eq!(inv.prune_empty(), 2);
    assert_eq!(inv.len(), 3);
    assert_eq!(inv.quantity("bolts"), 100);
    assert_eq!(inv.quantity("nuts"), 2);
    assert_eq!(inv.prune_empty(), 0);
}

#[test]
fn low_stock_is_sorted() {
    let inv = sample();
    assert_eq!(inv.low_stock(5), vec!["nuts", "screws", "washers"]);
}
