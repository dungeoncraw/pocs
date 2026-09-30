use challenge_11_missing_trait_bound::*;

#[test]
fn largest_ints() {
    assert_eq!(largest(&[3, 9, 2]), Some(9));
}

#[test]
fn largest_floats_and_chars() {
    assert_eq!(largest(&[1.5, -2.0, 0.25]), Some(1.5));
    assert_eq!(largest(&['x', 'b', 'z', 'a']), Some('z'));
}

#[test]
fn largest_empty() {
    let empty: [i32; 0] = [];
    assert_eq!(largest(&empty), None);
}

#[test]
fn describe_all_joins() {
    assert_eq!(describe_all(&[1, 2, 3]), "1, 2, 3");
    assert_eq!(describe_all(&["a", "b"]), "a, b");
}

#[test]
fn report_text() {
    assert_eq!(report(&[1, 2, 3]), "largest of [1, 2, 3] is 3");
    assert_eq!(report::<u8>(&[]), "no items");
}
