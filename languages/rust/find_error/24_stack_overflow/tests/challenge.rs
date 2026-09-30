use challenge_24_stack_overflow::*;

#[test]
fn small_list_works() {
    let list = List::counting(10);
    assert_eq!(list.len(), 10);
    assert_eq!(list.sum(), 55);
}

#[test]
fn empty_list() {
    let list = List::new();
    assert!(list.is_empty());
    assert_eq!(list.len(), 0);
    assert_eq!(list.sum(), 0);
}

#[test]
fn large_list_len_and_sum() {
    let n = 1_000_000u64;
    let list = List::counting(n);
    assert_eq!(list.len(), n as usize);
    assert_eq!(list.sum(), n * (n + 1) / 2);
}

#[test]
fn large_list_can_be_dropped() {
    let list = List::counting(1_000_000);
    assert!(!list.is_empty());
    drop(list);
}
