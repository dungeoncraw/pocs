use challenge_29_nan_sort_panic::*;

fn board() -> Vec<Entry> {
    vec![entry("ann", 3.5), entry("bob", f64::NAN), entry("cy", 9.0), entry("dee", 7.25), entry("eve", f64::NAN)]
}

#[test]
fn ranking_of_clean_data() {
    let clean = vec![entry("a", 1.0), entry("b", 3.0), entry("c", 2.0)];
    let names: Vec<_> = ranking(&clean).into_iter().map(|e| e.name).collect();
    assert_eq!(names, ["b", "c", "a"]);
}

#[test]
fn ranking_ignores_nan_entries() {
    let names: Vec<_> = ranking(&board()).into_iter().map(|e| e.name).collect();
    assert_eq!(names, ["cy", "dee", "ann"]);
}

#[test]
fn top_n_skips_invalid_readings() {
    assert_eq!(top_n(&board(), 2), ["cy", "dee"]);
    assert_eq!(top_n(&board(), 10), ["cy", "dee", "ann"]);
}

#[test]
fn max_score_ignores_nan() {
    assert_eq!(max_score(&board()), Some(9.0));
    assert_eq!(max_score(&[entry("x", f64::NAN)]), None);
    assert_eq!(max_score(&[]), None);
}

#[test]
fn median_ignores_nan() {
    assert_eq!(median(&[3.0, f64::NAN, 1.0, 2.0]), Some(2.0));
    assert_eq!(median(&[4.0, 1.0, f64::NAN, 3.0, 2.0]), Some(2.5));
    assert_eq!(median(&[f64::NAN, f64::NAN]), None);
}

#[test]
fn mean_ignores_nan() {
    assert_eq!(mean(&[1.0, f64::NAN, 3.0]), Some(2.0));
    assert_eq!(mean(&[f64::NAN]), None);
    assert_eq!(mean(&[]), None);
}
