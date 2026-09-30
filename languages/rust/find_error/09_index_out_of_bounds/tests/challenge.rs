use challenge_09_index_out_of_bounds::*;

#[test]
fn moving_average_basic() {
    let avg = moving_average(&[1.0, 2.0, 3.0, 4.0, 5.0], 3);
    assert_eq!(avg, vec![2.0, 3.0, 4.0]);
}

#[test]
fn moving_average_window_one() {
    assert_eq!(moving_average(&[4.0, 8.0], 1), vec![4.0, 8.0]);
}

#[test]
fn moving_average_window_equals_len() {
    assert_eq!(moving_average(&[2.0, 4.0, 6.0], 3), vec![4.0]);
}

#[test]
fn moving_average_degenerate_inputs() {
    assert!(moving_average(&[1.0, 2.0], 0).is_empty());
    assert!(moving_average(&[1.0, 2.0], 3).is_empty());
    assert!(moving_average(&[], 1).is_empty());
}

#[test]
fn window_max_works() {
    assert_eq!(window_max(&[1, 3, 2, 5, 4], 2), vec![3, 3, 5, 5]);
}
