use challenge_01_immutable_assign::*;

#[test]
fn running_totals_accumulate() {
    assert_eq!(running_totals(&[10, 20, 5]), vec![10, 30, 35]);
    assert!(running_totals(&[]).is_empty());
}

#[test]
fn clamp_percent_caps_at_100() {
    assert_eq!(clamp_percent(150), 100);
    assert_eq!(clamp_percent(30), 30);
}

#[test]
fn checkout_applies_discount() {
    assert_eq!(checkout(&[100, 100, 50], 20), 200);
    assert_eq!(checkout(&[100], 500), 0);
    assert_eq!(checkout(&[], 10), 0);
}
