use challenge_04_borrow_conflict::*;

#[test]
fn appends_copy_and_returns_first() {
    let mut v = vec!["alpha".to_string(), "beta".to_string()];
    assert_eq!(append_copy_of_first(&mut v), Some("alpha".to_string()));
    assert_eq!(v, vec!["alpha", "beta", "alpha-copy"]);
}

#[test]
fn empty_list_is_untouched() {
    let mut v: Vec<String> = vec![];
    assert_eq!(append_copy_of_first(&mut v), None);
    assert!(v.is_empty());
}

#[test]
fn append_many_counts() {
    let mut v = vec!["x".to_string()];
    assert_eq!(append_many(&mut v, 3), 3);
    assert_eq!(count_copies(&v), 3);
    assert_eq!(v.len(), 4);
}
